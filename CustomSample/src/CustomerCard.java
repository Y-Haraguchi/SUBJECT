
class CustomerCard {
	static int nextId = 1001;	//次に使用するID番号を管理するためのクラス変数、初期値は「1001」
	int c_id;					//顧客ID
	String c_name;				//顧客氏名
	String c_address;			//顧客住所名
	double c_shoe_Size;			//顧客靴サイズ

	//ID以外の入力あった場合のコンストラクタを作成
	CustomerCard(String name, String address, double shoe_Size){
		System.out.println("コンストラクタが呼び出されました");
		//コンストラクタ内でメイン関数から受け取った値を代入（初期化）
		this.c_id = CustomerCard.nextId;				//idにクラス変数を代入
		CustomerCard.nextId++;							//管理するidが重複しないようにインクリメント
		this.c_name = name;								//引数から氏名を代入
		this.c_address = address;
		this.c_shoe_Size = shoe_Size;
	}

	//氏名だけの引数だった場合のコンストラクタを作成
	CustomerCard(String name){
		this(name, "", 0.0);
	}

	//登録された情報の出力を行うメソッド
	void PrintCardInfo(){
		System.out.println("ID : " + c_id);
		System.out.println("顧客氏名 : " + c_name);
		System.out.println("顧客住所 : " + c_address);
		System.out.println("靴サイズ : " + c_shoe_Size);
		System.out.println("============================================");
	}
}
