# Chess ♟️

A fully playable chess game written from scratch in **Java + Swing**.

## What this project means to me

This was my **first real project ever** — the one where I actually learned to program.

I've always liked to *go big first*. Instead of starting with little exercises, I picked something I genuinely cared about so I'd have the motivation to push through: I really like chess, so I decided I was going to *build* chess. That love for the game is what kept me going when things got hard.

And it got hard a lot. I built this back in **2021, before AI assistants existed**, so every problem was solved the old way: reading, searching, trial and error, and a lot of stubbornness. Castling, en passant, pin detection, promotion, checkmate vs. stalemate — every rule was a small research project of its own. I put a *lot* of work into it.

This is my foundation. It's where programming stopped being a subject and became something I love doing. I have a lot of nostalgia for it, and I'm keeping it exactly as the milestone it was — rough edges and all.

---

## How it's structured

The codebase is split into two worlds: the **engine** (the rules of chess, pure logic) and the **GUI** (what you see and click). The engine knows nothing about Swing; the GUI just asks the engine questions and draws the answers.

```
com.chess
├── Chess.java                  # main() — boots the GUI
├── engine
│   ├── board
│   │   ├── Board.java          # the whole game state (immutable) + Builder
│   │   └── Tile.java           # a single square (EmptyTile / OccupiedTile)
│   ├── piece
│   │   ├── Piece.java          # abstract base + PieceType enum
│   │   ├── Pawn / Knight / Bishop / Rook / Queen / King
│   ├── move
│   │   └── Move.java           # abstract Move + every move type + MoveFactory
│   ├── player
│   │   ├── Player.java         # abstract base (check / checkmate / stalemate)
│   │   ├── WhitePlayer.java
│   │   └── BlackPlayer.java
│   └── util
│       ├── Alliance.java       # WHITE / BLACK enum with behavior
│       ├── BoardUtils.java     # rank/file lookup tables, constants
│       └── RoundButton.java    # custom Swing button
└── gui
    └── GUI.java                # ...all of it. (see "The visual layer" below)
```

### Classes and abstractions

The design leans heavily on **abstract classes and polymorphism** so the rest of the code can treat pieces, tiles, moves, and players uniformly without caring about the specific kind.

- **`Tile`** is abstract with two final subclasses: `EmptyTile` and `OccupiedTile`. Instead of null-checking a piece everywhere, you ask the tile `isTileOccupied()`. A static factory `Tile.createTile(coord, piece)` decides which one you get.

- **`Piece`** is abstract. Each piece type (`Pawn`, `Knight`, `Bishop`, `Rook`, `Queen`, `King`) extends it and implements:
  - `calculateLegalMoves(board)` — the movement rules for that piece, using candidate-offset math over the 0–63 board.
  - `movePiece(move)` — returns a **new** piece sitting on the destination square.

  Piece identity also lives in a `PieceType` enum, where even `isKing()` / `isRook()` are abstract methods overridden per constant — so there are no `instanceof` checks scattered around.

- **`Move`** is abstract, with a concrete subclass for *every kind of move* the rules need: `MajorMove`, `MajorAttackMove`, `PawnJumpMove`, `EnPassantMove`, `PawnPromotionMove`, `PawnPromotionAttackMove`, and `KingCastleMove`. Each one knows how to `executeMove()` (and `unmakeMove()`) by building the resulting board itself. A `MoveFactory` looks up the legal move that matches a clicked origin/destination.

- **`Player`** is abstract; `WhitePlayer` and `BlackPlayer` fill in the alliance-specific details (castling squares, which pieces are theirs, who the opponent is). The base class derives `isInCheck()`, `isInCheckMate()`, and `isInStaleMate()` from the move lists.

- **`Alliance`** is an enum that *carries behavior* rather than being a flag: `WHITE`/`BLACK` each implement `getDirection()` (which way pawns move), `choosePlayer(...)`, and `isWhite()/isBlack()`. This keeps "which side am I" logic out of `if` statements.

### Immutability and Builders ⭐

This is the part I'm most proud of architecturally, and it's the idea that made the whole engine click for me.

**The `Board` is immutable.** Once a `Board` object exists, it never changes. All of its fields are `final`, the tile list is wrapped in `Collections.unmodifiableList(...)`, and the piece lists are unmodifiable too. There is no `setPiece` on a live board.

So how do you *make a move* if the board can't change? **You don't mutate — you build a brand-new board.** That's where the **Builder pattern** comes in:

```java
public static class Builder {
    Map<Integer, Piece> boardConfig = new HashMap<>();
    Alliance moveMaker;
    Pawn enPassantPawn;

    public void setPiece(final Piece piece) { ... }
    public void setMoveMaker(final Alliance moveMaker) { ... }
    public Board build() { return new Board(this); }
}
```

Every `Move.executeMove(...)` walks all 64 squares, copies every piece that *isn't* involved in the move onto a fresh `Builder`, places the moved piece on its new square, flips whose turn it is, and calls `build()`. The result is a completely new `Board` representing the position *after* the move — the old one is left untouched.

This buys a lot:
- **No accidental state corruption.** The past never changes out from under you.
- **Trivial "what if" analysis.** Check detection works by literally playing the move on a throwaway board and asking if the king is attacked (`Move.leavesPlayerInCheck`). Since boards are immutable, that test board can't pollute the real game.
- **Pieces are immutable too** — `movePiece()` returns a *new* piece with `isFirstMove = false` instead of mutating the original, which is exactly what castling and pawn-jump rules rely on.

Pieces are placed onto the board through the builder; the `Board` constructor is **private**, so the *only* way to get a board is through `Builder.build()` (or the convenience factory `Board.createStandardBoard()`). The construction path is funneled and controlled.

It's a functional-flavored core hiding inside a Java OOP project, and figuring out *why* immutability made everything else simpler was one of those moments where programming really clicked for me.

---

## The visual layer (it's horrendous, and I know it 😅)

Full honesty: the GUI is **one ~1,500-line class**, [`GUI.java`](Chess/src/main/java/com/chess/gui/GUI.java), and it is *rough*.

- It's a single monster class with a pile of nested inner panels: `BoardPanel`, `TilePanel`, `PromotionPanel`, `WhiteCapturedPiecesPanel`, `BlackCapturedPiecesPanel`, `PlayAgainPanel`.
- **Absolute positioning everywhere.** `setLayout(null)` and hard-coded pixel coordinates (`setBounds(5, 860, 100, 25)`, magic numbers like `100`, `410`, `816 x 983`). Nothing is responsive; the window isn't even resizable.
- The promotion popup is positioned with a **giant `switch` of hand-written pixel coordinates** for every file, doubled for the flipped board.
- The "captured pieces" panels are five near-identical copy-pasted loops, one per piece type.
- Every interaction is a `MouseListener` with four empty methods and all the logic crammed into `mousePressed`, and the main move-handling block is nested *deep*.
- Game state and view state are tangled together in the same class.

In other words: it works, it looks decent on screen, and under the hood it's the kind of code you write when you're learning and just trying to make the thing *go*. The clean ideas live in the engine; the GUI is where the "I'll fix it later" energy went. I'm leaving it as-is — it's an honest snapshot of where I was.

But hey — it has piece images, move sounds (different sounds for captures, castling, checks, promotions), legal-move highlights, board flipping, captured-piece trays, check highlighting, and a play-again screen. For a first project, I was proud of how *alive* it felt to play.

---

## How it plays

It's a full, rules-complete two-player chess game on one screen:

- All standard piece movement and captures
- **Castling** (king- and queen-side, with all the square-safety checks)
- **En passant**
- **Pawn promotion** with a piece-picker popup (Q / N / R / B, or cancel)
- **Check, checkmate, and stalemate** detection with an end-game screen
- Legal-move highlighting, board flipping, captured-piece trays, and move sounds

The UI text is in **Spanish** (*Blancas / Negras*, *Partida finalizada*, *Tablas*, *Volver a jugar*) — my native language at the time.

---

## Running it

The project is built with **Maven** and targets **Java 18**.

```bash
# from the Chess/ directory
mvn clean package
mvn exec:java        # main class: com.chess.Chess
```

There's also a prebuilt `Chess.jar` and a `start.bat` in the repo root for a quick launch:

```bash
java -jar Chess.jar
```

---

*Built in 2021 as my first real project. Kept around for the nostalgia, and because it's where I started loving programming.*
