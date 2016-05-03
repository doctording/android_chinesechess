package com.algorithm;

public class SearchEngine{
	
	int CurPosition[][] = new int[10][9];// 搜索时用于当前节点棋盘状态的数组
	
	Chessmov m_cmBestMove; //记录最佳走法的变量
	
	MoveGenerator m_pMG; // 走法产生器
	
	Evaluation m_pEval; //估值函数
	
	int m_nSearchDepth; //最大搜索深度
	
	int m_nMaxDepth;// 当前搜索的最大搜索深度
	
	/**
	 * 根据传入的走法,改变棋盘
	 * @param mov
	 * @return
	 */
	int Makemove(Chessmov mov){
		int nChessID = CurPosition[mov.To.y][mov.To.x];
		CurPosition[mov.To.y][mov.To.x] = CurPosition[mov.From.y][mov.From.x];
		CurPosition[mov.From.y][mov.From.x] = Chessconst.NOCHESS;
		return nChessID;
	}
	/**
	 * 根据传入的走法,恢复到上一个棋盘
	 * @param mov
	 * @param nChessID
	 */
	void UnMakemove(Chessmov mov,int nChessID)
	{
		CurPosition[mov.From.y][mov.From.x] = CurPosition[mov.To.y][mov.To.x];
		CurPosition[mov.To.y][mov.To.x] = nChessID;
	}
	/**
	 * 判断游戏是否结束
	 * 如未结束，返回0，否则返回极大/极小值
	 * @param position
	 * @param nDepth
	 * @return
	 */
	public int IsGameOver(int position[][], int nDepth)
	{
		int i , j ;
		boolean RedLive = false, BlackLive=false; 
		
		for (i = 7; i < 10; i++)
			for (j = 3; j < 6; j++)
			{
				if (position[i][j] == Chessconst.B_KING)
					BlackLive = true;
				if (position[i][j] == Chessconst.R_KING)
					RedLive  = true;
			}

		for (i = 0; i < 3; i++)
			for (j = 3; j < 6; j++)
			{
				if (position[i][j] == Chessconst.B_KING)
					BlackLive = true;
				if (position[i][j] == Chessconst.R_KING)
					RedLive  = true;
			}

		i = (m_nMaxDepth - nDepth + 1) % 2;
		
		if (!RedLive)
			if (i != 0)
				return 19990 + nDepth;
			else
				return -19990 - nDepth;
		if (!BlackLive)
			if (i != 0)
				return -19990 - nDepth;
			else
				return 19990 + nDepth;
		return 0;
	}
	
	public int[][] getCurPosition() {
		return CurPosition;
	}
	public void setCurPosition(int[][] curPosition) {
		CurPosition = curPosition;
	}
	public Chessmov getM_cmBestMove() {
		return m_cmBestMove;
	}
	public void setM_cmBestMove(Chessmov m_cmBestMove) {
		this.m_cmBestMove = m_cmBestMove;
	}
	public MoveGenerator getM_pMG() {
		return m_pMG;
	}
	public void setM_pMG(MoveGenerator m_pMG) {
		this.m_pMG = m_pMG;
	}
	public Evaluation getM_pEval() {
		return m_pEval;
	}
	public void setM_pEval(Evaluation m_pEval) {
		this.m_pEval = m_pEval;
	}
	public int getM_nSearchDepth() {
		return m_nSearchDepth;
	}
	public void setM_nSearchDepth(int m_nSearchDepth) {
		this.m_nSearchDepth = m_nSearchDepth;
	}
	public int getM_nMaxDepth() {
		return m_nMaxDepth;
	}
	public void setM_nMaxDepth(int m_nMaxDepth) {
		this.m_nMaxDepth = m_nMaxDepth;
	}
	
}

