
public class Overloadexample {

	public static void main(String[] args) {
		//PointXYクラスのインスタンスを生成する(p1)
		PointXY p1 = new PointXY();
		//setメソッドを使わないで出力確認
		System.out.println("p1の座標は（" + p1.x + " , " + p1.y + "）");

		//PointXYクラスのインスタンスを生成する(p2)
		PointXY p2 = new PointXY(2, 10);
		//setメソッドを使わないで出力確認
		System.out.println("p2の座標は（" + p2.x + " , " + p2.y + "）");

		//PointXYクラスのインスタンスを生成する(p3)
		PointXY p3 = new PointXY(p2);
		//setメソッドを使わないで出力確認
		System.out.println("p2の座標は（" + p3.x + " , " + p3.y + "）");

	}

}
