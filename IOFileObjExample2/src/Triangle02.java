import java.io.Serializable;

//Pointクラスを作成
class Point implements Serializable{
	int x;
	int y;

	//コンストラクタ作成
	Point(int x, int y){
		this.x = x;
		this.y = y;

	}
}

//三角形の座標
class Triangle implements Serializable{
	Point p0;
	Point p1;
	Point p2;
}
