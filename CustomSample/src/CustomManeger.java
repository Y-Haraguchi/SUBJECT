
public class CustomManeger {

	public static void main(String[] args) {
		//インスタンスの配列の生成
		CustomerCard[] c_data = new CustomerCard[100];
		//コンストラクタで初期化
		c_data[0] = new CustomerCard("原口　ゆうぎ", "埼玉県", 27.5);
		c_data[1] = new CustomerCard("星　浩司", "埼玉県", 27.0);
		c_data[2] = new CustomerCard("吉岡　駿", "東京", 26.5);

		for(int i = 0 ; i < c_data.length ; i++){
			//nullチェックして中身がnullだったらループから抜ける
			if(c_data[i] == null){
				break;
			}
			System.out.println( i + "番目の顧客番号");
			//PrintCardInfoメソッドを呼び出して登録された情報をコンソールに出力
			c_data[i].PrintCardInfo();
			//System.out.println("==============================================");

		}

	}

}
