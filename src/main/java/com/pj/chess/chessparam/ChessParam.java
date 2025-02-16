package com.pj.chess.chessparam;

import com.pj.chess.BitBoard;
import com.pj.chess.ChessConstant;

import static com.pj.chess.ChessConstant.*;

/**
 * @author pengjiu 为防止多线程下，一些所需要的参数变量同步问题
 */
public class ChessParam {
    //每个棋子对应attackAndDefenseChesses 的下标表
    public static final int[] indexOfAttackAndDefense = new int[] {0,
        0, 1, 1, 0, 0, 0, 1,
        0, 1, 1, 0, 0, 0, 1
    };
    public int[] board;     // 棋盘->棋子
    public int[] allChess; //棋子->棋盘
    //	public int redBaseScore=0;  //红方分数

    //	public int blackBaseScore=0; //黑方分数
    public int[] baseScore = new int[2];
    public int[] boardBitRow; //位棋盘  行
    public int[] boardBitCol; //位棋盘  列
    //所有棋子位棋盘
    public BitBoard maskBoardCheeses;
    //各自的位棋盘
    public BitBoard[] bitBoards;
    //各自按角色分类的位棋盘[角色]
    public BitBoard[] maskBoardPersonalRoleChesses;
    private int[] boardRemainChess; //剩余棋子数量
    //[玩家][0攻击棋子数量  1防御棋子数量]
    private int[][] attackAndDefenseChesses = new int[2][2];

    public ChessParam(int[] board, int[] allChess, int[] baseScore, int[] boardBitRow, int[] boardBitCol,
                      int[] boardRemainChess, BitBoard maskBoardCheeses, BitBoard[] bitBoards,
                      BitBoard[] maskBoardPersonalRoleChesses) {
        this.board = board;
        this.allChess = allChess;
        this.baseScore = baseScore;
        this.boardBitRow = boardBitRow;
        this.boardBitCol = boardBitCol;
        this.boardRemainChess = boardRemainChess;
        this.maskBoardCheeses = maskBoardCheeses;
        this.bitBoards = bitBoards;
        this.maskBoardPersonalRoleChesses = maskBoardPersonalRoleChesses;
    }

    public ChessParam(ChessParam param) {
        this.copyToSelf(param);
    }



    public void copyToSelf(ChessParam param) {
        //棋子copy
        int[] allChessTemp = param.allChess;
        this.allChess = new int[allChessTemp.length];
        System.arraycopy(allChessTemp, 0, this.allChess, 0, allChessTemp.length);
        //棋盘copy
        int[] boardTemp = param.board;
        this.board = new int[boardTemp.length];
        System.arraycopy(boardTemp, 0, this.board, 0, boardTemp.length);
        //位棋盘行
        int[] boardBitRowTemp = param.boardBitRow;
        this.boardBitRow = new int[boardBitRowTemp.length];
        System.arraycopy(boardBitRowTemp, 0, this.boardBitRow, 0, boardBitRowTemp.length);
        //位横向列
        int[] boardBitColTemp = param.boardBitCol;
        this.boardBitCol = new int[boardBitColTemp.length];
        System.arraycopy(boardBitColTemp, 0, this.boardBitCol, 0, boardBitColTemp.length);
        //棋子数量
        int[] boardRemainChessTemp = param.boardRemainChess;
        this.boardRemainChess = new int[boardRemainChessTemp.length];
        System.arraycopy(boardRemainChessTemp, 0, this.boardRemainChess, 0, boardRemainChessTemp.length);
        // 攻击性棋子和防御性棋子数量
        int[][] attackAndDefenseChessesTemp = param.attackAndDefenseChesses;
        for (int i = 0; i < attackAndDefenseChessesTemp.length; i++) {
            System.arraycopy(attackAndDefenseChessesTemp[i], 0, this.attackAndDefenseChesses[i], 0,
                attackAndDefenseChessesTemp[i].length);
        }
        //所有子力的位棋盘
        this.maskBoardCheeses = new BitBoard(param.maskBoardCheeses);

        this.bitBoards = new BitBoard[param.bitBoards.length];
        //各方子力的位棋盘
        this.bitBoards[ChessConstant.REDPLAYSIGN] = new BitBoard(param.bitBoards[ChessConstant.REDPLAYSIGN]);
        this.bitBoards[ChessConstant.BLACKPLAYSIGN] = new BitBoard(param.bitBoards[ChessConstant.BLACKPLAYSIGN]);
        //各方子力按角色分类
        maskBoardPersonalRoleChesses = new BitBoard[param.maskBoardPersonalRoleChesses.length];
        for (int i = 0; i < param.maskBoardPersonalRoleChesses.length; i++) {
            this.maskBoardPersonalRoleChesses[i] = new BitBoard(param.maskBoardPersonalRoleChesses[i]);
        }

        //分数
        this.baseScore[ChessConstant.REDPLAYSIGN] = param.baseScore[ChessConstant.REDPLAYSIGN];
        this.baseScore[ChessConstant.BLACKPLAYSIGN] = param.baseScore[ChessConstant.BLACKPLAYSIGN];

    }

    private int getPlayByChessRole(int chessRole) {
        return chessRole > ChessConstant.REDKING ? ChessConstant.BLACKPLAYSIGN : ChessConstant.REDPLAYSIGN;
    }

    public int getChessesNum(int chessRole) {
        return boardRemainChess[chessRole];
    }

    public int getChessesNum(int play, int chessRole) {
        return boardRemainChess[getRoleIndexByPlayRole(play, chessRole)];
    }

    /**
     * @param chessRole 棋子角色 减少棋子数量
     */
    public void reduceChessesNum(int chessRole) {
        boardRemainChess[chessRole]--;
        attackAndDefenseChesses[getPlayByChessRole(chessRole)][indexOfAttackAndDefense[chessRole]]--;
    }

    /**
     * @param chessRole 柜子角色 增加棋子数量
     */
    public void increaseChessesNum(int chessRole) {
        boardRemainChess[chessRole]++;
        int play = getPlayByChessRole(chessRole);
        attackAndDefenseChesses[play][indexOfAttackAndDefense[chessRole]]++;
    }

    /**
     * @return 所有棋子数量
     */
    public int getAllChessesNum() {
        int num = 0;
        for (int i : boardRemainChess) {
            num += i;
        }
        return num;
    }

    //所有攻击棋子数量
    public int getAttackChessesNum(int play) {
        return attackAndDefenseChesses[play][0];
    }

    //所有防御棋子数量
    public int getDefenseChessesNum(int play) {
        return attackAndDefenseChesses[play][1];
    }

    public BitBoard getBitBoardByPlayRole(int play, int role) {
        return maskBoardPersonalRoleChesses[this.getRoleIndexByPlayRole(play, role)];
    }

    public int getRoleIndexByPlayRole(int play, int role) {
        return role = play == ChessConstant.REDPLAYSIGN ? role : (role + 7);
    }

    public void initChessBaseScoreAndNum() {

        for (int i = 16; i < allChess.length; i++) {
            if (allChess[i] != NOTHING) {
                int site = allChess[i];
                int chessRole = chessRoles[board[allChess[i]]];
                int play = i < 32 ? BLACKPLAYSIGN : REDPLAYSIGN;
                increaseChessesNum(chessRole);
                maskBoardCheeses.assignXor(MaskChesses[site]);
                bitBoards[play].assignXor(MaskChesses[site]);
                maskBoardPersonalRoleChesses[chessRole].assignXor(MaskChesses[site]);
            }
        }

    }
}











