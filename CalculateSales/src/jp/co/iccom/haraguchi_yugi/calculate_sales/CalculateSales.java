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

			//コマンドライン引数の数が「1」以外の場合
			if(!(args.length == 1)){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}

			//ファイルパスを指定
			File branchFile = new File(args[0],"branch.lst");

			//エラーチェック→ディレクトリを見に行った際に「branch.lst」がなかったら処理を終了させる
			if(!branchFile.isFile() || !branchFile.exists()){
				System.out.println("支店定義ファイルが存在しません");
				return;
			}
			//読み込み処理
			FileReader branchFr = new FileReader(branchFile);
			BufferedReader blanchBr = new BufferedReader(branchFr);

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> branchMap = new HashMap<String,String>();

			//売上ファイル内の値段を支店コードと紐付けるためのHashMapを生成
			HashMap<String, Long> branchSalesMap = new HashMap<String, Long>();

			//一行ずつ読み込むための一時格納変数
			String branchLine;

			try{
				/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
				* splitで保持されたデータをHashMapのキーと値に設定する
				* 読込先がnullなったらループを抜ける処理
				*/

				while((branchLine = blanchBr.readLine()) != null){
					String[] branchstr = branchLine.split(",", 0);
					/*エラーチェック→ファイルフォーマットが不正か
					*正規表現を用いてチェックし不正だったら処理を終了させる
					*/
					if(!(branchstr.length == 2) || !(branchstr[0].matches("^\\d{3}$"))){
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					//定義ファイル内のデータをセット
					branchMap.put(branchstr[0], branchstr[1]);

					//集計に使用するHashMapを生成
					branchSalesMap.put(branchstr[0], (long)0);
				}
			}catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				try{
					if(blanchBr != null){
						blanchBr.close();
					}
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
			//---------------------------------------------------------------------------------
			//商品定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------
			//ファイルパスを指定
			File commodFile = new File(args[0],"commodity.lst");

			//エラーチェック→ディレクトリを見に行った際に「commodity.lst」がなかったら処理を終了させる
			if(!commodFile.isFile() || !commodFile.exists()){
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

			try{
				/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
				* splitで保持されたデータをHashMapのキーと値に設定する
				* 読込先がnullなったらループを抜ける処理
				*/
				while((commodLine = commodBr.readLine()) != null){
					String[] commodstr = commodLine.split(",");

					/*エラーチェック→ファイルフォーマットが不正か
					*正規表現を用いてチェックし不正だったら処理を終了させる
					*/
					if(!(commodstr.length == 2) || !(commodstr[0].matches("^\\w{8}$"))){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					//定義ファイル内のデータを登録
					commodMap.put(commodstr[0], commodstr[1]);
					//集計に使用するマップの初期化
					commodSalesMap.put(commodstr[0], (long)0);
				}
			}catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				if(blanchBr != null){
					blanchBr.close();
				}
				if(commodBr != null){
					commodBr.close();
				}

			}
			//---------------------------------------------------------------------------------
			//売上ファイルの連番チェック
			//---------------------------------------------------------------------------------
			//フォルダのパスを指定
			File dir = new File(args[0]);

			//for文で連番ファイルを読み込む
			//フォルダ内のファイル名取得→「00000001.rcd」の部分
			File[] fileList = dir.listFiles();

			//ファイル内のデータを格納するためのArrayListを生成
			ArrayList<String> salesList = new ArrayList<String>();

			//一行ずつ読み込むための一時格納変数
			String salesLine;

			//売上ファイルの読み込み処理
			FileReader salesFr = null;
			BufferedReader salesBr = null;

			//「.rcd」抽出用リスト
			ArrayList<String> fileNameList = new ArrayList<String>();

			//連番チェック用リスト
			ArrayList<Long> fNumList = new ArrayList<Long>();

			try{
				//繰り返し処理で「.rcd」だけを抽出
				for(int i = 0 ; i < fileList.length ; i++){

					//「.rcd」の拡張子の場合のみif文の中を実行
					if(fileList[i].getName().matches("^\\d{8}\\.rcd$") && fileList[i].isFile()){
						//連番チェックの為、「.rcd」の拡張子がついたファイルのみ抽出
						fileNameList.add(fileList[i].getName());
					}
				}

				//if文で連番チェック→fileNameListとカウンターの「i」の差が「1」以外は処理を終了させる
				for(int i = 0 ; i < fileNameList.size() ; i++){

					//差の計算を行うためにファイル名を数値部分のみ取り出してadd
					fNumList.add(Long.parseLong(fileNameList.get(i).substring(0, 8)));

					//差が1以外の場合は連番になっていません
					if(!fileList[i].getName().matches("^\\d{8}.rcd$") || !((fNumList.get(i) - i) == 1)){
						System.out.println("売上ファイル名が連番になっていません");
						return;
					}
				}
			}catch(Exception e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				try{
					if(blanchBr != null){
						blanchBr.close();
					}
					if(commodBr != null){
						commodBr.close();
					}
					if(salesBr != null){
						salesBr.close();
					}
					if(salesFr != null){
						salesFr.close();
					}
				}catch(IOException e2){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
			//---------------------------------------------------------------------------------
			//売上ファイルの読み込み及び保持、計算を行う
			//---------------------------------------------------------------------------------
			try{
				for(int i = 0 ; i < fileList.length ; i++){

					//ファイルパス指定して1ファイルずつ読み込み
					File salesFile = new File(args[0],fileList[i].getName());

					//読み込み範囲は拡張子が「.rcd」のファイルだけ読み込み
					if(fileList[i].getName().matches("^\\d{8}\\.rcd$") && fileList[i].isFile()){

						//売上ファイルの読み込み処理
						salesFr = new FileReader(salesFile);
						salesBr = new BufferedReader(salesFr);

						//1ファイル内のデータをすべてaddしていく
						while((salesLine = salesBr.readLine()) != null){
							salesList.add(salesLine);
						}

						//要素数のチェック→要素数が3以外はエラーを返す
						if(fileList[i].getName().matches("^\\d{8}\\.rcd$") && !(salesList.size() == 3)){
							System.out.println(fileList[i].getName() + "のフォーマットが不正です" );
							return;
						}

						//計算を行う前に売上ファイルの３行目に数字以外の文字列が入っていたらエラーを返して処理を終了
						if(salesList.get(2).matches("^\\d$")){
							return;
						}

						/*すでに生成されている支店売上マップと商品売上マップと
						*読み込んできたファイル内のデータを比較して各コードが一致したら
						*それぞれのマップの値をインクリメントする
						 */
						//支店コードチェックを行う
						if(!(branchSalesMap.containsKey(salesList.get(0)))){
							System.out.println(fileList[i].getName() + "の支店コードが不正です");
							return;
						}
						//支店コードに紐づく売上金を加算する処理
						if(branchSalesMap.containsKey(salesList.get(0))){
							//支店売上金額をint型の計算用変数にキャストして格納
							long branchValue = Long.parseLong(salesList.get(2));
							branchValue += branchSalesMap.get(salesList.get(0));
							branchSalesMap.put(salesList.get(0), branchValue);

							//branchSalesMapに保持されている合計金額が10桁以上の場合エラーを返す
							if(String.valueOf(branchValue).length() > 10){
								System.out.println("合計金額が10桁を超えました");
								return;
							}
						}

						//商品コードが不正の場合、エラーメッセージ後に処理を終了
						if(!commodSalesMap.containsKey(salesList.get(1))){
							System.out.println(fileList[i] + "の商品コードが不正です");
							return;
						}
						//商品コードに紐づく売上金を加算する処理
						if(commodSalesMap.containsKey(salesList.get(1))){
							//商品売上金額をint型の計算用変数にキャストして格納
							long commodValue = Long.parseLong(salesList.get(2));
							commodValue += commodSalesMap.get(salesList.get(1));
							commodSalesMap.put(salesList.get(1), commodValue);
							//commodSalesMapに保持されている合計金額が10桁以上の場合エラーを返す
							if(String.valueOf(commodValue).length() > 10){
								System.out.println("合計金額が10桁を超えました");
								return;
							}
						}
						//ArrayListの要素をクリア
						salesList.clear();
					}else{
						//売上ファイル以外が読み込まれたらここに来て計算処理をbreakして次の処理へ
						break;
					}
				}
			}catch(NumberFormatException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				try{
					if(blanchBr != null){
						blanchBr.close();
					}
					if(commodBr != null){
						commodBr.close();
					}
					if(salesBr != null){
						salesBr.close();
					}
					if(salesFr != null){
						salesFr.close();
					}
				}catch(IOException e2){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
			//---------------------------------------------------------------------------------
			//集計結果をソート処理
			//---------------------------------------------------------------------------------
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

			//支店別ファイル出力用のWriterを生成
			File branchOutfile = null;
			FileWriter branchOutfw = null;
			BufferedWriter branchOutbw = null;
			PrintWriter branchOutpw = null;

			//商品別ファイル出力用のWriterを生成
			File commodOutfile = null;
			FileWriter commodOutfw = null;
			BufferedWriter commodOutbw = null;
			PrintWriter commodOutpw = null;

			try{
				//branch.outというファイルを出力
				branchOutfile = new File(args[0],"branch.out");
				branchOutfw = new FileWriter(branchOutfile);
				branchOutbw = new BufferedWriter(branchOutfw);
				branchOutpw = new PrintWriter(branchOutbw);

				//commodity.outというファイルを出力
				commodOutfile = new File(args[0],"commodity.out");
				commodOutfw = new FileWriter(commodOutfile);
				commodOutbw = new BufferedWriter(commodOutfw);
				commodOutpw = new PrintWriter(commodOutbw);

				//書き込み先のファイルが存在しなかったら処理を終了
				if(!branchOutfile.isFile() && !commodOutfile.isFile()){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

				//Mapで保持されている支店関係のデータをファイルに書き込み
				for (Entry<String,Long> branchEn : branchSortEntries) {
					//書き込むデータが空でないならば書き込み処理を行う
					if(!branchEn.getKey().isEmpty() && !branchMap.get(branchEn.getKey()).isEmpty() && branchEn.getValue() == null){
						System.out.println("予期せぬエラーが発生しました");
						return;
					}
					branchOutpw.println(branchEn.getKey() + "," + branchMap.get(branchEn.getKey()) + "," + branchEn.getValue());
				}
				//Mapで保持されている商品関係のデータをファイルに書き込み
				for (Entry<String,Long> commodEn : commodSortEntries) {
					//書き込むデータが空でないならば書き込み処理を行う
					if(!commodEn.getKey().isEmpty() && !commodMap.get(commodEn.getKey()).isEmpty() && commodEn.getValue() == null){
						System.out.println("予期せぬエラーが発生しました");
						return;
					}
					commodOutpw.println(commodEn.getKey() + "," + commodMap.get(commodEn.getKey()) + "," + commodEn.getValue());
				}

			}catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				if(blanchBr != null){
					blanchBr.close();
				}
				if(branchOutpw != null){
					branchOutpw.close();
				}
				if(branchOutbw != null){
					branchOutbw.close();
				}
				if(branchOutfw != null){
					branchOutfw.close();
				}
				if(salesBr != null){
					salesBr.close();
				}
				if(commodBr != null){
					commodBr.close();
				}
				if(commodOutpw != null){
					commodOutpw.close();
				}
				if(commodOutbw != null){
					commodOutbw.close();
				}
				if(commodOutfw != null){
					commodOutfw.close();
				}
			}
		}catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}

}
