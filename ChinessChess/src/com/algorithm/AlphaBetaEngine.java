package com.algorithm;

/**
 * Alpha-Beta 搜索算法
 */
public class AlphaBetaEngine extends SearchEngine{
	
	public AlphaBetaEngine(){
		super();
	}
	
	public void SearchAGoodMove(int position[][])
	{
		for(int i=0;i<10;i++){  // 保存当前棋盘信息
			for(int j=0;j<9;j++){
				CurPosition[i][j] = position[i][j];
			}
		}
		m_nMaxDepth = m_nSearchDepth; //当前最大深度
		
		/*
		 *  调用算法，得到 BestMove
		 *  一个负 很大， 一个 正 很大
		 */
		alphabeta(m_nMaxDepth, -20000, 20000); 
		
		Makemove(m_cmBestMove); //走下一步
		
		for(int i=0;i<10;i++){    // 棋子移动后，棋盘信息更改
			for(int j=0;j<9;j++){
				position[i][j] = CurPosition[i][j];
			}
		}
	}

	/**
	 * 黑方负最大 ， 红方正最大
	 */
	public int alphabeta(int depth, int alpha, int beta)
	{
		int i = IsGameOver(CurPosition, depth);
		if (i != 0)
			return i;

		if (depth <= 0)	//叶子节点取估值
			return m_pEval.Eveluate(CurPosition, (m_nMaxDepth-depth)%2);
		
		int count = m_pMG.CreatePossibleMove(CurPosition, depth, (m_nMaxDepth-depth)%2);

		for (i=0;i<count;i++) 
		{

			int type = Makemove(m_pMG.m_MoveList[depth][i]);
			int score = -alphabeta(depth - 1, -beta, -alpha); // 递归
			UnMakemove(m_pMG.m_MoveList[depth][i],type); 

			if (score > alpha)
			{
				alpha = score;
				if(depth == m_nMaxDepth)
					m_cmBestMove = m_pMG.m_MoveList[depth][i];
			}
	        if (alpha >= beta)  
	              break;
		}
		return alpha;
	}
}
