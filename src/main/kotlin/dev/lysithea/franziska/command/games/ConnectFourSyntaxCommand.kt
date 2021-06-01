package dev.lysithea.franziska.command.games

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction
import dev.lysithea.franziska.command.CommandCategory
import dev.lysithea.franziska.command.SyntaxContext
import dev.lysithea.franziska.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.PermissionState
import dev.lysithea.franziska.core.permission.UsageArea
import dev.lysithea.franziska.dsl.createMessage
import dev.lysithea.franziska.dsl.getMentionedUser
import dev.lysithea.franziska.utils.getAverageColour
import dev.lysithea.franziska.utils.toInputStream
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO

class ConnectFourSyntaxCommand : AbstractSyntaxCommand() {
    override val name: String
        get() = "c4"
    override val description: String
        get() = "Starts a game of Connect Four."
    override val permission: PermissionLevel
        get() = PermissionLevel.ANY
    override val category: CommandCategory
        get() = CommandCategory.GENERAL
    override val usageArea = UsageArea.GUILD_MESSAGE


    private val rand = Random()
    private val boards = mutableMapOf<MessageChannelBehavior, GameBoard>()

    override suspend fun execute(context: SyntaxContext) {
        val args = context.args

        val board = boards[context.channel]
        when (context.args[0].lowercase()) {
            "invite" -> {
                if ("cancel" in args) {
                    cancelGame(board, context)
                    return
                }
                invitePlayer(board, context)
                return
            }
            "accept", "decline" -> {
                acceptOrDecline(board, context)
                return
            }
            "play" -> {
                playMove(board, context)
                return
            }
        }
    }

    private fun playMove(board: GameBoard?, context: SyntaxContext) {
        context.franziska.launch {
            if (context.args.size != 2) {
                context.channel.createMessage(
                    Embeds.error(
                        "Invalid syntax",
                        "Example: ${context.commandWithPrefix} play A"
                    )
                )
                return@launch
            }
            if (board == null || board.turn == 0) {
                context.channel.createMessage("There is currently no pending or ongoing game.")
                return@launch
            }
            if (context.member !in listOf(board.player1, board.player2)) {
                context.channel.createMessage("You're not participating in the current game.")
                return@launch
            }
            if (board.getPlayer() != context.member) {
                context.channel.createMessage("It's not your turn.")
                return@launch
            }

            val playerIndex = board.turn
            val col = columns.indexOf(context.args[1].uppercase())
            if (col == -1) {
                context.channel.createMessage(
                    Embeds.error(
                        "Invalid column",
                        "Must be a letter Must be a letter between A and ${('A' + WIDTH - 1)}"
                    )
                )
                return@launch
            }
            board.play(col, playerIndex)
            val victory = board.victory
            val message = if (victory != null) {
                boards.remove(context.channel)
                if (victory.direction != GameBoard.Direction.DRAW) {
                    "${board.getPlayer(victory.player).displayName} has won the game."
                } else {
                    "Draw? Ok."
                }
            } else {
                board.turn = if (playerIndex == 1) 2 else 1
                "Make your move, ${(if (board.turn == 1) board.player1 else board.player2).displayName}."
            }

            board send context.channel.createMessage {
                content = message
                addFile("c4board.png", board.renderBoard().toInputStream())
            }

            try {
                context.event.message.delete()
            } catch (e: Throwable) {
                // probably no permission, ignore.
            }
        }
    }

    private fun acceptOrDecline(board: GameBoard?, context: SyntaxContext) {
        context.franziska.launch {
            if (board == null || board.player2 != context.member) {
                context.channel.createMessage("This invite is not for you.")
                return@launch
            }
            if (board.turn != 0) {
                context.channel.createMessage("This game has already started.")
                return@launch
            }
            if (context.args[0].equals("accept", true)) {
                board.turn = if (rand.nextBoolean()) 1 else 2
                board send context.channel.createMessage {
                    content = "${board.player1.mention}, your invite has been accepted. First turn goes to ${
                        board.getPlayer(board.turn).username
                    }. Play your move with `${context.commandWithPrefix} play <column>`."
                    addFile("c4board.png", board.renderBoard().toInputStream())
                }
            } else {
                boards.remove(context.channel)
                board send context.channel.createMessage("Your invitation has been declined, ${board.player1.mention}.")

            }
        }
    }

    private fun invitePlayer(board: GameBoard?, context: SyntaxContext) {
        context.franziska.launch {
            if (board != null && board.player1 != context.member) {
                context.channel.createMessage("Game or invite already in progress.")
                return@launch
            }
            if (context.args.size != 2) {
                context.channel.createMessage("You must specify a player to invite.")
                return@launch
            }
            val mentionedUser = context.args[1].getMentionedUser(context.event.getGuild()!!)
            if (mentionedUser == null) {
                context.channel.createMessage("User not found.")
                return@launch
            }
            if (mentionedUser.isBot) {
                context.channel.createMessage("Can't invite bots to play.")
                return@launch
            }
            GameBoard(context, context.member, mentionedUser, context.channel).run {
                boards[context.channel] = this
                this send context
                    .channel
                    .createMessage(
                        "${mentionedUser.mention}!! ${context.member.displayName} " +
                                "has invited you to play a game of Connect 4. " +
                                "Type `${context.commandWithPrefix} accept` or `${context.commandWithPrefix} decline` to respond to this request."
                    )
            }

        }
    }

    private fun cancelGame(board: GameBoard?, context: SyntaxContext) {
        context.franziska.launch {
            if (board == null) {
                context.channel.createMessage("There is currently no pending or ongoing games.")
                return@launch
            }
            if (board.player1 != context.member) {
                when (context.permissionHandler.hasAccess(PermissionLevel.MOD, context.member)) {
                    PermissionState.ACCEPTED -> {
                    }
                    else -> return@launch
                }
            }
            boards.remove(context.channel)
            context.event.message.addReaction(Emojis.thumbsup)
        }
    }

    companion object {
        val log = KotlinLogging.logger { }

        const val WIDTH = 7
        const val HEIGHT = 6
        const val CELLSIZE = 30
        val columns = ('A' until 'A' + WIDTH).map(Char::toString)

        fun resizeImage(img: BufferedImage, size: Int): BufferedImage {
            return if (img.width != size || img.height != size) {
                val resized = BufferedImage(size, size, img.type)
                val srcx = ((img.width - size) / 2).coerceAtLeast(0)
                val srcy = ((img.height - size) / 2).coerceAtLeast(0)
                val dstx = ((size - img.width) / 2).coerceAtLeast(0)
                val dsty = ((size - img.height) / 2).coerceAtLeast(0)
                resized.createGraphics().let { g2d ->
                    g2d.background = Color(img.getRGB(0, 0), true)
                    g2d.clearRect(0, 0, size, size)
                    log.trace(
                        "Resizing image (${img.width}x${img.height} to ${size}x$size. $dstx:$dsty-${size - dstx}:${size - dsty}" +
                                "-> $srcx:$srcy-${img.width - srcx}:${img.height - srcy}"
                    )
                    g2d.drawImage(
                        img,
                        dstx, dsty, size - dstx, size - dsty,
                        srcx, srcy, img.width - srcx, img.height - srcy, null
                    )
                }
                resized
            } else img
        }

        val baseBoard: BufferedImage by lazy {
            BufferedImage(CELLSIZE * WIDTH, CELLSIZE * (HEIGHT + 1), BufferedImage.TYPE_4BYTE_ABGR).apply {
                val g2d = createGraphics()
                g2d.background = Color(14, 43, 110)
                g2d.clearRect(0, 0, width, height)
                g2d.color = Color.WHITE
                g2d.font = g2d.font.deriveFont(Font.BOLD, 25f)
                val metrics = g2d.getFontMetrics(g2d.font)

                for (i in 0 until WIDTH)
                    ('A' + i).toString().let { str ->
                        g2d.drawString(
                            str, (CELLSIZE * (i + .5)).toInt() - metrics.stringWidth(str) / 2,
                            (CELLSIZE - metrics.height) / 2 + metrics.ascent
                        )
                    }

                g2d.color = Color(0)
                g2d.stroke = BasicStroke(3f)

                for (i in 0..WIDTH) {
                    g2d.drawLine(CELLSIZE * i, CELLSIZE, CELLSIZE * i, height)
                }

                for (j in 1..HEIGHT + 1)
                    g2d.drawLine(0, CELLSIZE * j, width, CELLSIZE * j)
            }
        }

        val playerCircles: Map<Int, BufferedImage> by lazy {
            mapOf(1 to "red-circle.png", 2 to "blue-circle.png")
                .mapValues {
                    ConnectFourSyntaxCommand::class.java.classLoader.getResource(it.value)
                        ?: throw IllegalStateException("No resource found for name $it")
                }
                .mapValues { ImageIO.read(it.value) ?: throw IllegalStateException("Couldn't read file $it") }
                .mapValues {
                    if (it.value.width != CELLSIZE || it.value.height != CELLSIZE || it.value.type != baseBoard.type) {
                        val img = BufferedImage(CELLSIZE, CELLSIZE, baseBoard.type)
                        img.createGraphics().let { graph ->
                            graph.background = Color(0, true)
                            graph.clearRect(0, 0, CELLSIZE, CELLSIZE)
                            graph.drawImage(it.value, 0, 0, CELLSIZE, CELLSIZE, null)
                        }
                        img
                    } else
                        it.value
                }
        }

        val drawOverlay: BufferedImage by lazy {
            val text = "DRAW"

            val image = BufferedImage(baseBoard.width, baseBoard.height, BufferedImage.TYPE_4BYTE_ABGR)
            val graph = image.createGraphics()

            graph.color = Color(80, 80, 80, 128)
            graph.fillRect(0, 0, image.width, image.height)

            graph.font = Font(Font.SANS_SERIF, Font.BOLD, 50)
            val metrics = graph.fontMetrics

            val textwidth = metrics.stringWidth(text)
            val textImage = BufferedImage(textwidth + 30, textwidth, BufferedImage.TYPE_4BYTE_ABGR)
            textImage.createGraphics().let { g2d ->
                g2d.font = graph.font
                g2d.color = Color(40, 40, 40, 192)
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
                g2d.drawString(text, 15, (textwidth - metrics.height) / 2 + metrics.ascent)
            }

            val transform = AffineTransformOp(
                AffineTransform.getRotateInstance(Math.toRadians(-30.0), textImage.width / 2.0, textImage.height / 2.0),
                AffineTransformOp.TYPE_BICUBIC
            )
            val rotatedText = BufferedImage(textImage.width, textImage.height, textImage.type)
            transform.filter(textImage, rotatedText)

            graph.drawImage(
                rotatedText,
                (image.width - rotatedText.width) / 2,
                (image.height - rotatedText.height + CELLSIZE) / 2,
                null
            )
            image
        }

    }

    class GameBoard(
        val context: SyntaxContext,
        val player1: Member,
        val player2: Member,
        val channel: MessageChannelBehavior
    ) {

        enum class Direction {
            RIGHT, DOWN, DOWNRIGHT, UPRIGHT, DRAW
        }

        data class Victory(val player: Int, val x: Int, val y: Int, val direction: Direction)

        //val lastAction: Temporal = LocalDateTime.now()
        private val gameBoard = Array(WIDTH * HEIGHT) { 0 }

        var turn: Int = 0
        private var lastMessage: Message? = null
        var victory: Victory? = null
            private set

        operator fun get(x: Int, y: Int): Int {
            assert(x in 0 until WIDTH && y in 0 until HEIGHT) { "Invalid coordinates (x: $x, y: $y, grid: $WIDTH x $HEIGHT)" }
            return gameBoard[y * WIDTH + x]
        }

        internal operator fun set(x: Int, y: Int, value: Int) {
            assert(x in 0 until WIDTH && y in 0 until HEIGHT) { "Invalid coordinates (x: $x, y: $y, grid: $WIDTH x $HEIGHT)" }
            gameBoard[y * WIDTH + x] = value
        }

        fun play(col: Int, playerindex: Int) {
            if (victory != null) {
                context.franziska.launch {
                    context.channel.createMessage("Game over.")
                }
            }

            val dropHeight = (0 until HEIGHT).firstOrNull { this[col, it] != 0 }?.minus(1) ?: HEIGHT - 1
            if (dropHeight == -1) {
                context.franziska.launch {
                    context.channel.createMessage("Game over.")
                }
            }
            this[col, dropHeight] = playerindex

            // Check Victory

            if ((0 until WIDTH).all { this[it, 0] != 0 })
                victory = Victory(-1, -1, -1, Direction.DRAW)
            else {
                victory = null
                for (y in 0 until HEIGHT) {
                    for (x in 0 until (WIDTH)) {
                        if (this[x, y] != 0) {
                            val player = this[x, y]
                            val rightbound = x + 1..x + 3
                            val upbound = y - 1 downTo y - 3
                            val downbound = y + 1..y + 3
                            when {
                                x < WIDTH - 3 && rightbound.all { this[it, y] == player } -> {
                                    log.debug("Won with horizontal from $x:$y")
                                    victory = Victory(player, x, y, Direction.RIGHT)
                                }
                                y < HEIGHT - 3 && downbound.all { this[x, it] == player } -> {
                                    log.debug("Won with vertical from $x:$y")
                                    victory = Victory(player, x, y, Direction.DOWN)
                                }
                                y < HEIGHT - 3 && x < WIDTH - 3 && rightbound.zip(downbound)
                                    .all { (i, j) -> this[i, j] == player } -> {
                                    log.debug("Won with bottom-right diagonal from $x:$y")
                                    victory = Victory(player, x, y, Direction.DOWNRIGHT)
                                }
                                y > 2 && x < WIDTH - 3 && rightbound.zip(upbound)
                                    .all { (i, j) -> this[i, j] == player } -> {
                                    log.debug("Won with t from $x:$y")
                                    victory = Victory(player, x, y, Direction.UPRIGHT)
                                }
                            }
                        }
                    }
                }
            }
        }

        fun renderBoard(): BufferedImage =
            BufferedImage(baseBoard.width, baseBoard.height, baseBoard.type).also { img ->
                img.data = baseBoard.data
                val g2d = img.createGraphics()
                for (j in 0 until HEIGHT)
                    for (i in 0 until WIDTH)
                        if (this@GameBoard[i, j] != 0) {
                            val circle = playerCircles.getValue(this[i, j])
                            g2d.drawImage(circle, i * CELLSIZE, (j + 1) * CELLSIZE, null)
                        }
                victory?.let { (player, x, y, direction) ->
                    if (direction == Direction.DRAW) {
                        g2d.drawImage(drawOverlay, 0, 0, null)
                    } else {
                        g2d.color = playerCircles.getValue(player).getAverageColour().darker()
                        g2d.stroke = BasicStroke(CELLSIZE / 5f)
                        val deltaX = if (direction == Direction.DOWN) 0 else 3
                        val deltaY = when (direction) {
                            Direction.DOWN, Direction.DOWNRIGHT -> 3
                            Direction.RIGHT -> 0
                            Direction.UPRIGHT -> -3
                            Direction.DRAW -> throw IllegalStateException("oh no")
                        }

                        g2d.drawLine(
                            x * CELLSIZE + CELLSIZE / 2, (y + 1) * CELLSIZE + CELLSIZE / 2,
                            (x + deltaX) * CELLSIZE + CELLSIZE / 2, (y + 1 + deltaY) * CELLSIZE + CELLSIZE / 2
                        )
                    }
                }
            }

        fun getPlayer(id: Int = turn) = if (id == 1) player1 else player2

        infix fun send(msg: Message) {
            context.franziska.launch {
                try {
                    lastMessage?.delete()
                } catch (throwable: Throwable) {
                    // Ignore - Probably don't have permission to delete.
                }
                lastMessage = msg
            }
        }
    }
}