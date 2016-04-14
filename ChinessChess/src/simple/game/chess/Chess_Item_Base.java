package simple.game.chess;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.util.MapAnnotation.Map;

public class Chess_Item_Base{

	@Map(key = "centx")
	private int centx;
	@Map(key = "centy")
	private int centy;
	@Map(key = "type")
	private int type = 0;
	@Map(key = "red")
	private boolean red;// 默认上红下黑
	@Map(key = "first")
	private boolean first;// 先手

	public Chess_Item_Base(){
		super();
	}
	
	public Chess_Item_Base(int x, int y, int type, boolean red) {
		this.centx = x;
		this.centy = y;
		this.type = type;
		this.red = red;
		this.first = red;
	}

	/**
	 * 先手，取反
	 */
	public void changeFirst()
	{
		first = !first;
	}
	
	public Chess_Item_Base(int x, int y) {
		this.centx = x;
		this.centy = y;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isRed() {
		return red;
	}
	// 是自己这一方   还是对方
	public boolean isSelf(Chess_Item_Base item) {
		if (item.isRed() == red)
			return true;
		return false;
	}

	public void setRed(boolean red) {
		this.red = red;
	}

	public int getCentx() {
		return centx;
	}

	public void setCentx(int centx) {
		this.centx = centx;
	}

	public int getCenty() {
		return centy;
	}

	public void setCenty(int centy) {
		this.centy = centy;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean inside(int x, int y) { 
		return centx == x && centy == y;
	}

	public String getText() {
		switch (this.type) {
			case 0:
				if (first)
					return "兵";
				return "卒";
			case 1:
				return "车";
			case 2:
				return "马";
	
			case 3:
				if(first)
					return "象";
				return "相";
			case 4:
				return "士";
			case 5:
				if (first)
					return "帅";
				return "将";
			case 6:
				return "炮";
			default:
				break;
		}
		return "";
	}

	public void addSteps(List<Chess_Item_Base> items,List<Chess_Item_Base> steps) {
		// 兵/卒的走法
		if (getType() == 0) {
			List<Chess_Item_Base> tempbin = new ArrayList<Chess_Item_Base>();
			if (isRed()) {
				tempbin.add(new Chess_Item_Base(getCentx(), getCenty() + 1));
				if (getCenty() > 4) {
					if (getCentx() < 8) {
						tempbin.add(new Chess_Item_Base(getCentx() + 1,getCenty()));
					}
					if (getCentx() > 0)
						tempbin.add(new Chess_Item_Base(getCentx() - 1,getCenty()));
				}
			} else {
				tempbin.add(new Chess_Item_Base(getCentx(), getCenty() - 1));
				if (getCenty() < 5) {
					if (getCentx() < 8) {
						tempbin.add(new Chess_Item_Base(getCentx() + 1,
								getCenty()));
					}

					if (getCentx() > 0) {
						tempbin.add(new Chess_Item_Base(getCentx() - 1,
								getCenty()));
					}
				}
			}
			a: for (Chess_Item_Base temp : tempbin) {
				if (temp.getCenty() < 0 || temp.getCenty() > 9) {
					continue;
				}
				for (Chess_Item_Base item : items) {
					if (item.inside(temp.getCentx(), temp.getCenty())) {
						if (!item.isSelf(this))
							steps.add(temp);

						continue a;
					}

				}
				steps.add(temp);
			}
		}
		// 车的走法
		if (getType() == 1) {
			a: for (int i = getCentx() - 1; i >= 0; i--)// 左边
			{
				for (Chess_Item_Base item : items) {
					if (item.inside(i, getCenty())) {
						if (!item.isSelf(this))
							steps.add(new Chess_Item_Base(i, centy));

						break a;
					}
				}
				steps.add(new Chess_Item_Base(i, centy));
			}
			b: for (int i = getCentx() + 1; i <= 8; i++)// 右边
			{
				for (Chess_Item_Base item : items) {
					if (item.inside(i, getCenty())) {
						if (!item.isSelf(this)) {
							steps.add(new Chess_Item_Base(i, centy));

						}
						break b;
					}
				}
				steps.add(new Chess_Item_Base(i, centy));
			}

			c: for (int i = getCenty() - 1; i >= 0; i--)// 上面
			{
				for (Chess_Item_Base item : items) {
					if (item.inside(centx, i)) {
						if (!item.isSelf(this))
							steps.add(new Chess_Item_Base(centx, i));
						break c;
					}

				}
				steps.add(new Chess_Item_Base(centx, i));
			}
			d: for (int i = getCenty() + 1; i <= 9; i++)// 上面
			{
				for (Chess_Item_Base item : items) {
					if (item.inside(centx, i)) {
						if (!item.isSelf(this))
							steps.add(new Chess_Item_Base(centx, i));

						break d;
					}
				}
				steps.add(new Chess_Item_Base(centx, i));
			}
		}
		// 炮的走法
		if (getType() == 6) {

			boolean hasjump = false;
			a: for (int i = getCentx() - 1; i >= 0; i--)// 左边
			{
				if (!hasjump)
					for (Chess_Item_Base item : items) {
						if (item.inside(i, getCenty())) {
							// if (!item.isSelf(this))
							// steps.add(new Chess_Item_Base(i, centy));
							hasjump = true;
							continue a;
						}
					}
				if (!hasjump) {
					steps.add(new Chess_Item_Base(i, centy));
				} else {
					for (Chess_Item_Base item : items) {
						if (item.inside(i, getCenty())) {
							if (!item.isSelf(this)) {
								steps.add(new Chess_Item_Base(i, centy));

							}
							break a;
						}
					}
				}
			}

			hasjump = false;
			b: for (int i = getCentx() + 1; i <= 8; i++)// 左边
			{
				if (!hasjump)
					for (Chess_Item_Base item : items) {
						if (item.inside(i, getCenty())) {
							// if (!item.isSelf(this))
							// steps.add(new Chess_Item_Base(i, centy));
							hasjump = true;
							continue b;
						}
					}
				if (!hasjump) {
					steps.add(new Chess_Item_Base(i, centy));
				} else {
					for (Chess_Item_Base item : items) {
						if (item.inside(i, getCenty())) {
							if (!item.isSelf(this)) {
								steps.add(new Chess_Item_Base(i, centy));

							}
							break b;
						}
					}
				}
			}

			hasjump = false;
			c: for (int i = getCenty() - 1; i >= 0; i--)// 上边
			{
				if (!hasjump)
					for (Chess_Item_Base item : items) {
						if (item.inside(getCentx(), i)) {
							// if (!item.isSelf(this))
							// steps.add(new Chess_Item_Base(i, centy));
							hasjump = true;
							continue c;
						}
					}
				if (!hasjump) {
					steps.add(new Chess_Item_Base(getCentx(), i));
				} else {
					for (Chess_Item_Base item : items) {
						if (item.inside(getCentx(), i)) {
							if (!item.isSelf(this)) {
								steps.add(new Chess_Item_Base(getCentx(), i));

							}
							break c;
						}
					}
				}
			}
			hasjump = false;
			d: for (int i = getCenty() + 1; i <= 9; i++)// 左边
			{
				if (!hasjump)
					for (Chess_Item_Base item : items) {
						if (item.inside(getCentx(), i)) {
							// if (!item.isSelf(this))
							// steps.add(new Chess_Item_Base(i, centy));
							hasjump = true;
							continue d;
						}
					}
				if (!hasjump) {
					steps.add(new Chess_Item_Base(getCentx(), i));
				} else {
					for (Chess_Item_Base item : items) {
						if (item.inside(getCentx(), i)) {
							if (!item.isSelf(this)) {
								steps.add(new Chess_Item_Base(getCentx(), i));

							}
							break d;
						}
					}
				}
			}
		}
		// 象的走法
		if (getType() == 3) {
			List<Chess_Item_Base> tempxiang = new ArrayList<Chess_Item_Base>();
			Chess_Item_Base item1 = new Chess_Item_Base(getCentx() + 2,
					getCenty() + 2);// 右下
			Chess_Item_Base item2 = new Chess_Item_Base(getCentx() - 2,
					getCenty() + 2);// 左下
			Chess_Item_Base item3 = new Chess_Item_Base(getCentx() + 2,
					getCenty() - 2);// 右上
			Chess_Item_Base item4 = new Chess_Item_Base(getCentx() - 2,
					getCenty() - 2);// 左上
			tempxiang.add(item1);
			tempxiang.add(item2);
			tempxiang.add(item3);
			tempxiang.add(item4);
			if (isRed())// 红方的话，旗子不能超过4
			{
				if (item1.getCenty() > 4 || item1.getCenty() < 0) {
					tempxiang.remove(item1);
				}
				if (item2.getCenty() > 4 || item2.getCenty() < 0) {
					tempxiang.remove(item2);
				}
				if (item3.getCenty() > 4 || item3.getCenty() < 0) {
					tempxiang.remove(item3);
				}
				if (item4.getCenty() > 4 || item4.getCenty() < 0) {
					tempxiang.remove(item4);
				}
			}
			if (!isRed())// 红方的话，旗子5-9
			{
				if (item1.getCenty() <= 4 || item1.getCenty() > 9) {
					tempxiang.remove(item1);
				}
				if (item2.getCenty() <= 4 || item2.getCenty() > 9) {
					tempxiang.remove(item2);
				}
				if (item3.getCenty() <= 4 || item3.getCenty() > 9) {
					tempxiang.remove(item3);
				}
				if (item4.getCenty() <= 4 || item4.getCenty() > 9) {
					tempxiang.remove(item4);
				}
			}
			a: for (Chess_Item_Base temp : tempxiang) {
				b: for (Chess_Item_Base item : items) {
					if (item.inside((temp.getCentx() + getCentx()) / 2,
							(temp.getCenty() + getCenty()) / 2)) {

						continue a;
					}
					if (item.inside(temp.getCentx(), temp.getCenty())) {
						if (!item.isSelf(this)) {
							steps.add(temp);

						}
						continue a;
					}
				}
				steps.add(temp);
			}

		}
		// 马的走法
		if (getType() == 2) {
			List<Chess_Item_Base> tempxiang = new ArrayList<Chess_Item_Base>();
			Chess_Item_Base item1 = new Chess_Item_Base(getCentx() + 2,
					getCenty() + 1);
			Chess_Item_Base item2 = new Chess_Item_Base(getCentx() + 1,
					getCenty() + 2);
			Chess_Item_Base item3 = new Chess_Item_Base(getCentx() - 2,
					getCenty() + 1);
			Chess_Item_Base item4 = new Chess_Item_Base(getCentx() - 1,
					getCenty() + 2);
			Chess_Item_Base item5 = new Chess_Item_Base(getCentx() + 2,
					getCenty() - 1);
			Chess_Item_Base item6 = new Chess_Item_Base(getCentx() + 1,
					getCenty() - 2);
			Chess_Item_Base item7 = new Chess_Item_Base(getCentx() - 2,
					getCenty() - 1);
			Chess_Item_Base item8 = new Chess_Item_Base(getCentx() - 1,
					getCenty() - 2);
			tempxiang.add(item1);
			tempxiang.add(item2);
			tempxiang.add(item3);
			tempxiang.add(item4);
			tempxiang.add(item5);
			tempxiang.add(item6);
			tempxiang.add(item7);
			tempxiang.add(item8);
			a: for (int i = 0; i < tempxiang.size(); i++) {
				Chess_Item_Base temp = tempxiang.get(i);
				if (temp.getCentx() < 0 || temp.getCenty() < 0
						|| temp.getCenty() > 9 || temp.getCentx() > 8) {
					tempxiang.remove(temp);
					i--;
					continue a;
				}
				int df = temp.getCenty() - centy;
				int dx = temp.getCentx() - centx;
				Chess_Item_Base piejiao = new Chess_Item_Base(-1, -1);
				if (df == -2 || df == 2) {
					piejiao = new Chess_Item_Base(getCentx(), getCenty() + df
							/ 2);
				}
				if (dx == -2 || dx == 2) {
					piejiao = new Chess_Item_Base(getCentx() + dx / 2,
							getCenty());
				}
				b: for (Chess_Item_Base item : items) {

					if (item.inside(piejiao.getCentx(), piejiao.getCenty())) {

						continue a;
					}
					if (item.inside(temp.getCentx(), temp.getCenty())) {
						if (!item.isSelf(this)) {
							steps.add(temp);

						}
						continue a;
					}
				}
				steps.add(temp);
			}

		}

		// 士的走法
		if (getType() == 4) {
			List<Chess_Item_Base> tempxiang = new ArrayList<Chess_Item_Base>();
			Chess_Item_Base item1 = new Chess_Item_Base(getCentx() + 1,
					getCenty() + 1);// 右下
			Chess_Item_Base item2 = new Chess_Item_Base(getCentx() - 1,
					getCenty() + 1);// 左下
			Chess_Item_Base item3 = new Chess_Item_Base(getCentx() + 1,
					getCenty() - 1);// 右上
			Chess_Item_Base item4 = new Chess_Item_Base(getCentx() - 1,
					getCenty() - 1);// 左上
			tempxiang.add(item1);
			tempxiang.add(item2);
			tempxiang.add(item3);
			tempxiang.add(item4);
			if (isRed())// 红方的话，旗子不能超过4
			{
				if (item1.getCenty() > 2 || item1.getCenty() < 0
						|| item1.getCentx() < 3 || item1.getCentx() > 5) {
					tempxiang.remove(item1);
				}
				if (item2.getCenty() > 2 || item2.getCenty() < 0
						|| item2.getCentx() < 3 || item2.getCentx() > 5) {
					tempxiang.remove(item2);
				}
				if (item3.getCenty() > 2 || item3.getCenty() < 0
						|| item3.getCentx() < 3 || item3.getCentx() > 5) {
					tempxiang.remove(item3);
				}
				if (item4.getCenty() > 2 || item4.getCenty() < 0
						|| item4.getCentx() < 3 || item4.getCentx() > 5) {
					tempxiang.remove(item4);
				}
			}
			if (!isRed())// 红方的话，旗子5-9
			{
				if (item1.getCenty() < 7 || item1.getCenty() > 9
						|| item1.getCentx() < 3 || item1.getCentx() > 5) {
					tempxiang.remove(item1);
				}
				if (item2.getCenty() < 7 || item2.getCenty() > 9
						|| item2.getCentx() < 3 || item2.getCentx() > 5) {
					tempxiang.remove(item2);
				}
				if (item3.getCenty() < 7 || item3.getCenty() > 9
						|| item3.getCentx() < 3 || item3.getCentx() > 5) {
					tempxiang.remove(item3);
				}
				if (item4.getCenty() < 7 || item4.getCenty() > 9
						|| item4.getCentx() < 3 || item4.getCentx() > 5) {
					tempxiang.remove(item4);
				}
			}
			a: for (Chess_Item_Base temp : tempxiang) {
				b: for (Chess_Item_Base item : items) {

					if (item.inside(temp.getCentx(), temp.getCenty())) {
						if (!item.isSelf(this)) {
							steps.add(temp);

						}
						continue a;
					}
				}
				steps.add(temp);
			}

		}

		// 将的走法
		if (getType() == 5) {
			List<Chess_Item_Base> tempxiang = new ArrayList<Chess_Item_Base>();
			Chess_Item_Base item1 = new Chess_Item_Base(getCentx(),
					getCenty() + 1);// 右下
			Chess_Item_Base item2 = new Chess_Item_Base(getCentx(),
					getCenty() - 1);// 左下
			Chess_Item_Base item3 = new Chess_Item_Base(getCentx() + 1,
					getCenty());// 右上
			Chess_Item_Base item4 = new Chess_Item_Base(getCentx() - 1,
					getCenty());// 左上
			tempxiang.add(item1);
			tempxiang.add(item2);
			tempxiang.add(item3);
			tempxiang.add(item4);
			if (isRed())// 红方的话，旗子不能超过4
			{
				if (item1.getCenty() > 2 || item1.getCenty() < 0
						|| item1.getCentx() < 3 || item1.getCentx() > 5) {
					tempxiang.remove(item1);
				}
				if (item2.getCenty() > 2 || item2.getCenty() < 0
						|| item2.getCentx() < 3 || item2.getCentx() > 5) {
					tempxiang.remove(item2);
				}
				if (item3.getCenty() > 2 || item3.getCenty() < 0
						|| item3.getCentx() < 3 || item3.getCentx() > 5) {
					tempxiang.remove(item3);
				}
				if (item4.getCenty() > 2 || item4.getCenty() < 0
						|| item4.getCentx() < 3 || item4.getCentx() > 5) {
					tempxiang.remove(item4);
				}
			}
			if (!isRed())// 红方的话，旗子5-9
			{
				if (item1.getCenty() < 7 || item1.getCenty() > 9
						|| item1.getCentx() < 3 || item1.getCentx() > 5) {
					tempxiang.remove(item1);
				}
				if (item2.getCenty() < 7 || item2.getCenty() > 9
						|| item2.getCentx() < 3 || item2.getCentx() > 5) {
					tempxiang.remove(item2);
				}
				if (item3.getCenty() < 7 || item3.getCenty() > 9
						|| item3.getCentx() < 3 || item3.getCentx() > 5) {
					tempxiang.remove(item3);
				}
				if (item4.getCenty() < 7 || item4.getCenty() > 9
						|| item4.getCentx() < 3 || item4.getCentx() > 5) {
					tempxiang.remove(item4);
				}
			}
			a: for (Chess_Item_Base temp : tempxiang) {
				b: for (Chess_Item_Base item : items) {

					if (item.inside(temp.getCentx(), temp.getCenty())) {
						if (!item.isSelf(this)) {
							steps.add(temp);

						}
						continue a;
					}
				}
				steps.add(temp);
			}

		}
	}

}