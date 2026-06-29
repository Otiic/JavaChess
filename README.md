# Chess ♟️

A fully playable chess game written from scratch in **Java + Swing**.

## What this project means

This was my **first project ever** — where I learnt to code.

I've always liked to *go big first*. Instead of starting with little exercises, I pick something I have a genuine interest about so that I'll have the motivation to push through: I really like chess, so I decided I was going to *build* chess as my first project, that easy. That is what got me through when I realized that it wasn't as easy as I thought, I had almost zero experience coding, so it was a real challengue.

And it got hard, quick. AI wasn´t a thing yet, so every problem was solved the old way: reading, searching, trial and error, and a lot of stubbornness. Castling, en passant, pin detection, promotion, checkmate vs. stalemate — every rule was a small research project of its own. I put a *lot* of work into it. Also want to mention the YouTube channel Software Architecture & Design and its playlist Java Chess Engine Tutorial, although incomplete (and bunch of bugs), it really laid the base and tought me the basics of chess programming, without it I don´t know if I would have been able.

This is my foundation. It's where I got all the logic of programming, and the love for it. I have a lot of nostalgia for it, and I'm keeping it exactly as the milestone it was — rough edges and all.

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

### Immutability and Builders

**The `Board` is immutable.** Once a `Board` object exists, it never changes. All of its fields are `final`, the tile list is wrapped in `Collections.unmodifiableList(...)`, and the piece lists are unmodifiable too. There is no `setPiece` on a live board.

## The visual layer

Tt's bad, but pretty (if it fits on your screen)

The GUI is a solid **one ~1,500-line class**, [`GUI.java`](Chess/src/main/java/com/chess/gui/GUI.java).

- It's a single monster class with a pile of nested inner panels.
- **Absolute positioning everywhere.** `setLayout(null)` and hard-coded pixel coordinates (`setBounds(5, 860, 100, 25)`, magic numbers like `100`, `410`, `816 x 983`). Nothing is responsive; the window isn't even resizable.
- The promotion popup is positioned with a **giant `switch` of hand-written pixel coordinates** for every file, doubled for the flipped board.
- Every interaction is a `MouseListener` with four empty methods and all the logic crammed into `mousePressed`, and the main move-handling block is nested *deep*.
- Game state and view state are tangled together in the same class.

In other words: it works, it looks good on screen, and under the hood it's the kind of code you write when you're learning and just trying to make the thing *go*. The clean ideas live in the engine; the GUI is the "Finish however I can"

But — it has piece images, move sounds (different sounds for captures, castling, checks, promotions), legal-move highlights, board flipping, captured-piece trays, check highlighting, and a play-again screen. I was proud of how it. I AM.

---

## How it plays

It's a full, rules-complete two-player chess game on one screen:

- All standard piece movement and captures
- **Castling** (king- and queen-side, with all the square-safety checks)
- **En passant**
- **Pawn promotion** with a piece-picker popup (Q / N / R / B, or cancel)
- **Check, checkmate, and stalemate** detection with an end-game screen
- Legal-move highlighting, board flipping, captured-piece trays, and move sounds

The UI is in **Spanish** (*Blancas / Negras*, *Partida finalizada*, *Tablas*, *Volver a jugar*) — my native language.

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

*Built in 2021 as my first project. Kept around for the nostalgia, and because it's where everything started.*
