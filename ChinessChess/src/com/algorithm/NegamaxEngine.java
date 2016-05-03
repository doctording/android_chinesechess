package com.algorithm;

/**
 * 极大极小法->负极大值法
 */
public class NegamaxEngine extends SearchEngine{
	
	public NegamaxEngine(){
		super();
	}
	
	public void SearchAGoodMove(int position[][])
	{
		m_nMaxDepth = m_nSearchDepth;

		for(int i=0;i<10;i++){  // 保存当前棋盘信息
			for(int j=0;j<9;j++){
				CurPosition[i][j] = position[i][j];
			}
		}
		
		NegaMax(m_nMaxDepth);
		
		Makemove(m_cmBestMove); //走下一步
		
		for(int i=0;i<10;i++){ // 棋子移动后，棋盘信息更改
			for(int j=0;j<9;j++){
				position[i][j] = CurPosition[i][j];
			}
		}
	}

	public int NegaMax(int depth)
	{
		int current = -20000 ;
		int i;

		i = IsGameOver(CurPosition, depth);
		if (i != 0)
			return i;

		if (depth <= 0)	//叶子节点取估值
	    	return m_pEval.Eveluate(CurPosition, (m_nMaxDepth-depth)%2);
		
		int Count = m_pMG.CreatePossibleMove(CurPosition, depth, (m_nMaxDepth-depth)%2);

		for (i=0;i<Count;i++) 
		{
			int type = Makemove(m_pMG.m_MoveList[depth][i]);
			int score = -NegaMax(depth - 1);
			UnMakemove(m_pMG.m_MoveList[depth][i],type); 
			
			if (score > current) // 得到最大值 返回
			{
				current = score;
				if(depth == m_nMaxDepth)
				{
					m_cmBestMove = m_pMG.m_MoveList[depth][i];
				}
			}

		}
		return current;
	}
	
}

