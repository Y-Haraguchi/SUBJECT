package jp.co.iccom.haraguchi_yugi.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//メイン処理
public class CalculateSales {
	public static void main(String[] args) {
		try{
			//---------------------------------------------------------------------------------
			//支店定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------
			//ファイルパスを指定
			File branchFile = new File(args[0],"branch.lst");

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

			//売上ファイル内の値段を支店コードと紐付けるためのHashMapを生成
			HashMap<String, Long> branchSalesMap = new HashMap<String, Long>();

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
					return;
				}
				//定義ファイル内のデータをセット
				branchMap.put(branchstr[0], branchstr[1]);

				//集計に使用するHashMapを生成
				branchSalesMap.put(branchstr[0], (long)0);

			}
			blanchBr.close();
			//---------------------------------------------------------------------------------
			//商品定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------
			//ファイルパスを指定
			File commodFile = new File(args[0],"commodity.lst");

			//エラーチェック→ディレクトリを見に行った際に「commodity.lst」がなかったら処理を終了させる
			if(!commodFile.exists()){
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			//読み込み処理
			FileReader commodFr = new FileReader(commodFile);
			BufferedReader commodBr = new BufferedReader(commodFr);

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> commodMap = new HashMap<String,String>();

			//売上ファイル内の値段を商品コードと紐付けるためのHashMapを生成
			HashMap<String, Long> commodSalesMap = new HashMap<String, Long>();


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
				//定義ファイル内のデータを登録
				commodMap.put(commodstr[0], commodstr[1]);

				//集計に使用するマップの初期化
				commodSalesMap.put(commodstr[0], (long)0);
			}
			commodBr.close();
			//---------------------------------------------------------------------------------
			//売上ファイルの読み込み及び保持、計算を行う
			//---------------------------------------------------------------------------------

			//for文で連番ファイルを読み込む
			//フォルダのパスを指定
			File dir = new File(args[0]);
			//フォルダ内のファイル名取得→「00000001.rcd」の部分
			String[] fileList = dir.list();

			//ファイル内のデータを格納するためのArrayListを生成
			ArrayList<String> salesList = new ArrayList<String>();

			//一行ずつ読み込むための一時格納変数
			String salesLine;

			//繰り替えしてファイルを読み込み
			for(int i = 0 ; i < fileList.length ; i++){

				//「.rcd」の拡張子の場合のみif文の中を実行
				if(fileList[i].matches("^.*.rcd.*$") && !dir.isDirectory()){

					//連番チェックの為、「.」で文字列を分割する
					String[] divstr = fileList[i].split("\\.");

					//連番チェックの為、divstr[0]番に入っている分割した文字列の数字をint型に変換
					long fileNameNum = Long.parseLong(divstr[0]);

					//if文で連番チェック→fileNameNumとカウンターの「i」の差が「1」以外は処理を終了させる
					if(!((fileNameNum - i) == 1)){
						System.out.println("ファイルが連番になっていません");
						return;
					}
				}
				//ファイルパス指定して1ファイルずつ読み込み
				File salesFile = new File(args[0],fileList[i]);

				//読み込み範囲は拡張子が「.rcd」のファイルだけ読み込み
				if(fileList[i].matches("^.*rcd.*$") && !salesFile.isDirectory()){

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
					if(fileList[i].matches("^.*.rcd.*$") && !(salesList.size() == 3)){
						System.out.println("<" + fileList[i] + ">のファイルフォーマットが不正です" );
						return;
					}
					/*すでに生成されている支店売上マップと商品売上マップと
					*読み込んできたファイル内のデータを比較して各コードが一致したら
					*それぞれのマップの値をインクリメントする
					 */
					//支店コードチェックを行う
					if(!(salesList.get(0).matches("^\\d{3}$"))){
						System.out.println("<" + fileList[i] + ">の支店コードが不正です");
						return;
					}
					//支店コードに紐づく売上金を加算する処理
					if(branchSalesMap.containsKey(salesList.get(0))){
						//支店売上金額をint型の計算用変数にキャストして格納
						long branchValue = Long.parseLong(salesList.get(2));
						branchValue += branchSalesMap.get(salesList.get(0));
						branchSalesMap.put(salesList.get(0), branchValue);
					}

					//商品コードが不正の場合、エラーメッセージ後に処理を終了
					if(!salesList.get(1).matches("^[0-9].*[a-zA-Z]|[a-zA-Z].*[0-9].\\d{3}$")){
						System.out.println("<" + fileList[i] + ">の商品コードが不正です");
						return;
					}
					//商品コードに紐づく売上金を加算する処理
					if(commodSalesMap.containsKey(salesList.get(1))){
						//商品売上金額をint型の計算用変数にキャストして格納
						long commodValue = Long.parseLong(salesList.get(2));
						commodValue += commodSalesMap.get(salesList.get(1));
						commodSalesMap.put(salesList.get(1), commodValue);
					}
					//ArrayListの要素をクリア
					salesList.clear();
				}
			}

			//---------------------------------------------------------------------------------
			//集計結果をソート処理
			//---------------------------------------------------------------------------------
			//ソート処理は以下のページを参考に
			//http://papiroidsensei.com/memo/java_map_sort.html
			//支店売上金額のソート用のListを生成
			List<Map.Entry<String, Long>> branchSortEntries = new ArrayList<Map.Entry<String, Long>>(branchSalesMap.entrySet());
			Collections.sort(branchSortEntries, new Comparator<Map.Entry<String,Long>>() {
			//オーバーライド@Override		//アノテーション
				public int compare(
						Entry<String,Long> entry1, Entry<String,Long> entry2) {
	                return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
	            }
	        });
	        //商品売上金額のソート用のListを生成
			List<Map.Entry<String, Long>> commodSortEntries = new ArrayList<Map.Entry<String, Long>>(commodSalesMap.entrySet());
			Collections.sort(commodSortEntries, new Comparator<Map.Entry<String,Long>>() {
				 //オーバーライド
				@Override		//アノテーション
	            public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
	                return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
	            }
	        });
			//---------------------------------------------------------------------------------
			//集計結果を出力
			//---------------------------------------------------------------------------------

			//支店コードに対応する支店名と売上を出力
			File branchOutfile = new File(args[0],"branch.out");
			FileWriter branchOutfw = new FileWriter(branchOutfile);
			BufferedWriter branchOutbw = new BufferedWriter(branchOutfw);
			PrintWriter branchOutpw = new PrintWriter(branchOutbw);

			 for (Entry<String,Long> branchEn : branchSortEntries) {
				 branchOutpw.println(branchEn.getKey() + "," + branchMap.get(branchEn.getKey()) + "," + branchEn.getValue());
			 }
			 branchOutpw.close();

			//商品コードに対応する商品名と売上を出力
			File commodOutfile = new File(args[0],"commodity.out");
			FileWriter commodOutfw = new FileWriter(commodOutfile);
			BufferedWriter commodOutbw = new BufferedWriter(commodOutfw);
			PrintWriter commodOutpw = new PrintWriter(commodOutbw);

			for (Entry<String,Long> commodEn : commodSortEntries) {
				commodOutpw.println(commodEn.getKey() + "," + commodMap.get(commodEn.getKey()) + "," + commodEn.getValue());
			}
			commodOutpw.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");
		}
	}

}
