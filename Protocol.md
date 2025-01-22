# Protocol

player1.("status", username1, "ready") -> server.status1(string, string, string);
player2.("status", username2, "ready") -> server.status2(string, string, string);
server.(game.getBoard()) -> player1.board(int[][]);
server.(game.getBoard()) -> player2.board(int[][]);
while !game.checkState()@server do {
    if check(playerTurn == player1)@server then
        server.("turn") -> player1.status(string);
        player1.(column) -> server.status1(int);
        while !game.validMove()@server do {
            server.("invalid") -> player1.status1(string);
            player1.(column) -> server.status1(int);
        }
    else
        server.("turn") -> player2.status(string);
        player2.(column) -> server.status2(int);
        while !game.validMove()@server do {
            server.("invalid") -> player2.status(string);
            player2.(column) -> server.status2(int);
        }
        server.(game.getBoard()) -> player1.board(int[][]);
        server.(game.getBoard()) -> player2.board(int[][]);
}
server.(game.lastMove()) -> player1.winner(int);
server.(game.lastMove()) -> player2.winner(int);

# Client projection

channel(player, server).put("status", userName, "ready");
board := channel(player, server).get(int[][]);
while true {
    branch := channel(player, server).get(string);
    if branch == "continue" then{
        branch := channel(player, server).get(string);
        if branch == "then" then {
            channel(player, server).put(column);
            while true {
                branch := channel(player, server).get(string);
                if branch == "continue" then {
                    channel(player, server).put(column);
                } else break;
            }
        }
        board := channel(player, server).get(int[][]);
    } else break;
}
winner := channel(player, server).get(int);

# Server projection

status1 := channel(player1, server).get(string, string, string);
status2 := channel(player2, server).get(string, string, string);
channel(player1, server).put(board);
channel(player2, server).put(board);
while !game.checkState() do {
    channel(player1, server).put("continue");
    channel(player2, server).put("continue");
    if check(playerTurn == player1) then {
        channel(player1, server).put("then");
        channel(player2, server).put("then");
        column := channel(player1, server).get(int);
        while !game.validMove do {
            channel(player1, server).put("continue");
            column := channel(player1, server).get(int);
        }
        channel(player1, server).put("break");
    } else {
        channel(player1, server).put("else");
        channel(player2, server).put("else");
        column := channel(player2, server).get(int);
        while !game.validMove do {
            channel(player2, server).put("continue");
            column := channel(player2, server).get(int);
        }
        channel(player2, server).put("break");
    }
    channel(player1, server).put(board);
    channel(player2, server).put(board);
}
channel(player1, server).put("break");
channel(player2, server).put("break");
channel(player1, server).put(winner);
channel(player2, server).put(winner);