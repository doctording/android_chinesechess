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
		//memcpy(CurPosition, position, 90);
		for(int i=0;i<10;i++){
			for(int j=0;j<9;j++){
				CurPosition[i][j] = position[i][j];
			}
		}
		m_nMaxDepth = m_nSearchDepth;
		alphabeta(m_nMaxDepth, -20000, 20000);
		Makemove(m_cmBestMove);
		//memcpy(position, CurPosition, 90);
		for(int i=0;i<10;i++){
			for(int j=0;j<9;j++){
				position[i][j] = CurPosition[i][j];
			}
		}
	}

	public int alphabeta(int depth, int alpha, int beta)
	{
		int score;
		int i;
		int type;

		i = IsGameOver(CurPosition, depth);
		if (i != 0)
			return i;

		if (depth <= 0)	//叶子节点取估值
			return m_pEval.Eveluate(CurPosition, (m_nMaxDepth-depth)%2);
		
		int count = m_pMG.CreatePossibleMove(CurPosition, depth, (m_nMaxDepth-depth)%2);

		for (i=0;i<count;i++) 
		{

			type = Makemove(m_pMG.m_MoveList[depth][i]);
			score = -alphabeta(depth - 1, -beta, -alpha);
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

