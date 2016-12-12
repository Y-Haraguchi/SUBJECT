package jp.co.iccom.haraguchi_yugi.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

//メイン処理
public class Calculate_Sales_Main {
	public static void main(String[] args) {
		System.out.println(args[0]);
		System.out.println(args[1]);
		try{
			//---------------------------------------------------------------------------------
			//支店定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------
			//ファイルパスを指定
			File branchFile = new File(args[0]);

			//読み込み処理
			FileReader branchFr = new FileReader(branchFile);
			BufferedReader blanchBr = new BufferedReader(branchFr);

			//エラーチェック→ディレクトリを見に行った際に「branch.lst」がなかったら処理を終了させる
			if(!branchFile.exists()){
				System.out.println("支店定義ファイルが存在しません");
				blanchBr.close();
				return;
			}

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> branchMap = new HashMap<String,String>();

			//一行ずつ読み込むための一時格納変数
			String branchLine;

			/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
			* splitで保持されたデータをHashMapのキーと値に設定する
			* 読込先がnullなったらループを抜ける処理
			*/

			while((branchLine = blanchBr.readLine()) != null){
				String[] branchstr = branchLine.split(",");
				/*エラーチェック→ファイルフォーマットが不正か
				*正規表現を用いてチェックし不正だったら処理を終了させる
				*/
				if(!(branchstr.length == 2) || !(branchstr[0].matches("^\\d{3}$"))){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					blanchBr.close();
					System.out.println(branchstr[0].matches("\\d{3}$"));
					System.out.println(branchstr.length);
					return;
				}
				branchMap.put(branchstr[0], branchstr[1]);

			}
			//デバッグ出力
			System.out.println("支店定義ファイルが正常に読み込まれました");
			blanchBr.close();
			//デバッグ出力
			System.out.println(branchMap.get("001"));
			System.out.println(branchMap.get("002"));
			System.out.println(branchMap.get("003"));
			System.out.println(branchMap.get("004"));

			System.out.println("==================================================");

			//---------------------------------------------------------------------------------
			//商品定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------

			//ファイルパスを指定
			File commodFile = new File(args[1]);

			//エラーチェック③→ディレクトリを見に行った際に「commodity.lst」がなかったら処理を終了させる
			if(!commodFile.exists()){
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			//読み込み処理
			FileReader commodFr = new FileReader(commodFile);
			BufferedReader commodBr = new BufferedReader(commodFr);

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> commodMap = new HashMap<String,String>();

			//一行ずつ読み込むための一時格納変数
			String commodLine;

			/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
			* splitで保持されたデータをHashMapのキーと値に設定する
			* 読込先がnullなったらループを抜ける処理
			*/

			while((commodLine = commodBr.readLine()) != null){
				String[] commodstr = commodLine.split(",");

				/*エラーチェック→ファイルフォーマットが不正か
				*正規表現を用いてチェックし不正だったら処理を終了させる
				*/
				if(!(commodstr.length == 2) || !(commodstr[0].matches("^[0-9].*[a-zA-Z]|[a-zA-Z].*[0-9].\\d{3}$"))){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					commodBr.close();
					return;
				}
				commodMap.put(commodstr[0], commodstr[1]);
			}
			//デバッグ出力
			System.out.println("商品定義ファイルが正常に読み込まれました");
			commodBr.close();
			//デバッグ出力
			System.out.println(commodMap.get("SFT00001"));
			System.out.println(commodMap.get("SFT00002"));
			System.out.println(commodMap.get("SFT00003"));
			System.out.println(commodMap.get("SFT00004"));
			System.out.println("==================================================");

			//---------------------------------------------------------------------------------
			//売上ファイルの読み込み及び保持、計算を行う
			//---------------------------------------------------------------------------------

			//for文で連番ファイルを読み込む
			//フォルダのパスを指定
			File dir = new File(args[2]);
			//フォルダ内のファイル名取得→「00000001.rcd」の部分
			String[] fileList = dir.list();

			//ファイル内のデータを格納するためのArrayListを生成
			ArrayList<String> salesList = new ArrayList<String>();

			//売上ファイル内の値段を支店コードと紐付けるためのHashMapを生成
			HashMap<String, Integer> branchSalesMap = new HashMap<String, Integer>();

			//売上ファイル内の値段を商品コードと紐付けるためのHashMapを生成
			HashMap<String, Integer> commodSalesMap = new HashMap<String, Integer>();

			//一行ずつ読み込むための一時格納変数
			String salesLine;

			//繰り替えしてファイルを読み込み
			for(int i = 0 ; i < fileList.length ; i++){

				//「.rcd」の拡張子の場合のみif文の中を実行
				if(fileList[i].matches("^.*.rcd.*$")){

					//連番チェックの為、「.」で文字列を分割する
					String[] divstr = fileList[i].split("\\.");

					//連番チェックの為、divstr[0]番に入っている分割した文字列の数字をint型に変換
					int fileNameNum = Integer.parseInt(divstr[0]);

					//if文で連番チェック→fileNameNumとカウンターの「i」の差が「1」以外は処理を終了させる
					if(!((fileNameNum - i) == 1)){
						System.out.println("ファイルが連番になっていません");
						return;
					}
					//デバッグ表示
					System.out.println("divstr[0]　=" + divstr[0]);
				}
				//ファイルパス指定して1ファイルずつ読み込み
				File salesFile = new File(args[2],fileList[i]);

				//読み込み範囲は拡張子が「.rcd」のファイルだけ読み込み
				if(!fileList[i].matches("^.*lst.*$")){

					//売上ファイルの読み込み処理
					FileReader salesFr = new FileReader(salesFile);
					BufferedReader salesBr = new BufferedReader(salesFr);

					//1ファイル内のデータをすべてaddしていく
					while((salesLine = salesBr.readLine()) != null){
						salesList.add(salesLine);
					}
					//salesBrをクローズ
					salesBr.close();

					//要素数のチェック→要素数が3以外はエラーを返す
					if(!(salesList.size() == 3)){
						System.out.println("<" + fileList[i] + ">のファイルフォーマットが不正です" );
						return;
					}


					//ここでHashMapでコードと値を登録する→登録する値は「0」

					//それぞれのマップの値をインクリメントする

					System.out.println("キーは一致してる？①　" + branchSalesMap.containsKey(salesList.get(0)));

					//支店コードチェックを行う
					if(!(salesList.get(0).matches("^\\d{3}$"))){
						System.out.println("<" + fileList[i] + ">の支店コードが不正です");
						return;
					//商品コードのチェックを行う
					}
					//addした要素を「支店コードと金額」と設定されたHashMapにセットしていく
					//2週目のループ以降にすでに登録されていたらelseifの処理を行う
					if(branchSalesMap.get(salesList.get(0)) == null || !branchSalesMap.containsKey(salesList.get(0))){
						branchSalesMap.put(salesList.get(0), Integer.parseInt(salesList.get(2)));
						//デバッグ表示
						System.out.println("キーは一致してる？　" + branchSalesMap.containsKey(salesList.get(0)));

					//キーが一致していたら、対応するコードのvalueに加算していく
					}else if(branchSalesMap.containsKey(salesList.get(0))){
						int buffValue = branchSalesMap.get(salesList.get(0));							//一時格納変数
						int sumValue = 0;				//現ループの合計を格納する変数

						//新しい値をput
						branchSalesMap.put(salesList.get(0), Integer.parseInt(salesList.get(2)));

						sumValue = buffValue + branchSalesMap.get(salesList.get(0));

						//加算した値をputして登録
						branchSalesMap.put(salesList.get(0), sumValue);

						//合計金額が10桁以上の場合エラーを返して処理を終了させる
						if(salesList.get(0).matches("^\\d{10,}$")){
							System.out.println("合計金額が10桁を超えました");
							return;
						}
					}
					//デバッグ表示
					System.out.println( branchSalesMap.entrySet());
					System.out.println( salesList.get(1));

					//商品コードが不正の場合、エラーメッセージ後に処理を終了
					if(!salesList.get(1).matches("^[0-9].*[a-zA-Z]|[a-zA-Z].*[0-9].\\d{3}$")){
						System.out.println("<" + fileList[i] + ">の商品コードが不正です");
						return;
					}
					//addした要素を「商品コードと金額」と設定されたHashMapにセットしていく
					//2週目のループ以降にすでに登録されていたらelseifの処理を行う
					if(commodSalesMap.get(salesList.get(1)) == null || !commodSalesMap.containsKey(salesList.get(1))){
						commodSalesMap.put(salesList.get(1), Integer.parseInt(salesList.get(2)));
						//デバッグ表示
						System.out.println("キーは一致してる？　" + commodSalesMap.containsKey(salesList.get(1)));

					//キーが一致していたら、対応するコードのvalueに加算していく
					}else if(commodSalesMap.containsKey(salesList.get(1))){
						int buffValue = commodSalesMap.get(salesList.get(1));							//一時格納変数
						int sumValue = 0;				//現ループの合計を格納する変数

						//新しい値をput
						commodSalesMap.put(salesList.get(1), Integer.parseInt(salesList.get(2)));

						sumValue = buffValue + commodSalesMap.get(salesList.get(1));

						//加算した値をputして登録
						commodSalesMap.put(salesList.get(1), sumValue);

						//合計金額が10桁以上の場合エラーを返して処理を終了させる
						if(salesList.get(1).matches("^\\d{10,}$")){
							System.out.println("合計金額が10桁を超えました");
							return;
						}
					}

					//デバッグ表示
					System.out.println( commodSalesMap.entrySet());


					//要素内をクリア
					salesList.clear();

					System.out.println(salesFile);
					System.out.println("=============================================");
				}
			}




			//---------------------------------------------------------------------------------
			//集計結果をソート処理
			//---------------------------------------------------------------------------------
			//ソート処理は以下のページを参考に
			//http://papiroidsensei.com/memo/java_map_sort.html



			//---------------------------------------------------------------------------------
			//集計結果を出力
			//---------------------------------------------------------------------------------

			//outPut先の拡張子を設定
			File branchOutfile = new File(args[2],"branch.out");
			FileWriter branchOutfw = new FileWriter(branchOutfile);
			BufferedWriter branchOutbw = new BufferedWriter(branchOutfw);
			PrintWriter branchOutpw = new PrintWriter(branchOutbw);

			//デバッグ出力
			//branchOutpw.format("");



			branchOutpw.close();

		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

}
