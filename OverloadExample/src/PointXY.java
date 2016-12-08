//PointXYクラスを作成
public class PointXY {
	static int counter = 0;
	int x;					//x座標
	int y;					//y座標

	//ｘとｙの値を0にするコンストラクタ
	PointXY(){
		//0で初期化
		this(0 ,0);
		//コンストラクタが呼び出された場合に出力
		System.out.println("0初期化のコンストラクタが呼び出されました");
		PointXY.counter++;

	}

	//PointXYクラスのインスタンスと同じ値に初期化するコンストラクタ
	PointXY(PointXY p){
		//引数で渡された値をインスタンス変数に代入（初期化）
		this(p.x,p.y);
		//コンストラクタが呼び出された場合に出力
		System.out.println("PointXYクラスのインスタンスと同じ値に初期化するコンストラクタが呼び出されました");
	}

	//int型のｘとｙの値をセットメソッドから受け取った値に初期化するコンストラクタ
	PointXY(int x, int y){
		//コンストラクタが呼び出された場合に出力
		System.out.println("int型の引数を初期化するコンストラクタが呼び出されました");
		//引数で渡された値をインスタンス変数に代入（初期化）
		this.x = x;
		this.y = y;
	}


	//メインメソッドから渡される引数をインスタンス変数に代入するメソッド
	void set(int x, int y){
		//int型の引数をインスタンス変数に代入
		this.x = x;
		this.y = y;

	}

	//メインメソッドから渡される引数をインスタンス変数に代入するメソッド（オーバーロード）
	void set(PointXY p){
		//引数で渡されるインスタンスの変数と同じものにする
		this.x = p.x;
		this.y = p.y;


	}

}
