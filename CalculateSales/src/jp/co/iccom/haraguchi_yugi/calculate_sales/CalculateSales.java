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

public class CalculateSales {

	//読み込み処理
	File defineFile = null;
	FileReader defineFr = null;
	BufferedReader defineBr = null;

	//支店定義ファイル内データをMapに値を保持するためのHashMapを生成
	static Map<String,String> defineBrMap = null;
	//商品定義ファイル内データをMapに値を保持するためのHashMapを生成
	static Map<String,String> defineCoMap = null;
	//売上ファイル内の値段を支店コードと紐付けるためのHashMapを生成
	static Map<String, Long> brSalesMap = null;
	//売上ファイル内の値段を商品コードと紐付けるためのHashMapを生成
	static Map<String, Long> coSalesMap = null;
	//一行ずつ読み込むための一時格納変数
	String dfineFileLine;

	//定義ファイル入力メソッド
	Boolean DefinFilesReader(String args, String filePath, String fileType, String regex,
			Map<String, String> dfMap, Map<String, Long> saMap) throws IOException{

		//引数からファイルパスを受け取る
		defineFile = new File(args, filePath);

		//エラーチェック→ディレクトリを見に行った際に「branch.lst」がなかったら処理を終了させる
		if(!defineFile.isFile() || !defineFile.exists()){
			System.out.println(fileType + "定義ファイルが存在しません");
			return false;
		}
		//ここにはif文で例外を投げる処理を入れる

		//ファイルの読み込み
		defineFr = new FileReader(defineFile);
		defineBr  = new BufferedReader(defineFr);

		while((dfineFileLine = defineBr.readLine()) != null){
			String[] strLine = dfineFileLine.split(",", 0);
			//エラーチェック→ファイルフォーマットが不正か
			if(!(strLine.length == 2) || !(strLine[0].matches(regex))){
				System.out.println(fileType + "定義ファイルのフォーマットが不正です");
				return false;
			}
			//定義ファイル内のデータをセット
			dfMap.put(strLine[0], strLine[1]);
			//集計に使用する値の初期化
			saMap.put(strLine[0], (long)0);
		}
		//処理が成功したらメインにtureを戻す
		return true;
	}

	//集計表ファイル出力メソッド
	Boolean SpreadSheetWriter(String args, String filePath,
			List<Entry<String, Long>> sortEntries, Map<String, String> dfineMap)throws IOException{

		File sheetOutfile = new File(args, filePath);
		FileWriter sheetOutfw = new FileWriter(sheetOutfile);
		BufferedWriter sheetOutbw = new BufferedWriter(sheetOutfw);
		PrintWriter sheetOutpw = new PrintWriter(sheetOutbw);

		try{
			//書き込み先のファイルが存在しなかったら処理を終了
			if(!sheetOutfile.isFile()){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
			//Mapで保持されている支店関係のデータをファイルに書き込み
			for(Entry<String, Long> sortEnMap : sortEntries) {
				//書き込むデータが空でないならば書き込み処理を行う
				if(!sortEnMap.getKey().isEmpty() && !dfineMap.get(sortEnMap.getKey()).isEmpty() && sortEnMap.getValue() == null){
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
				sheetOutpw.println(sortEnMap.getKey() + "," + dfineMap.get(sortEnMap.getKey()) + "," + sortEnMap.getValue());
			}

		}finally{
			sheetOutpw.close();
			sheetOutbw.close();
			sheetOutfw.close();
		}
		return true;

	}

	public static void main(String[] args) {
		try{
			//読み込みメソッドを使うためのインスタンスの生成
			CalculateSales dfr = new CalculateSales();

			//各マップを生成
			CalculateSales.defineBrMap = new HashMap<String, String>();
			CalculateSales.brSalesMap = new HashMap<String, Long>();
			CalculateSales.defineCoMap = new HashMap<String, String>();
			CalculateSales.coSalesMap = new HashMap<String, Long>();

			//---------------------------------------------------------------------------------
			//支店定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------

			//コマンドライン引数の数が「1」以外の場合
			if(!(args.length == 1)){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			try{
				//支店定義ファイルを読み込むためメソッドを呼び出し
				dfr.DefinFilesReader(args[0], "branch.lst", "支店", "^\\d{3}$", defineBrMap, brSalesMap);
				if(!dfr.DefinFilesReader(args[0], "branch.lst", "支店", "^\\d{3}$", defineBrMap, brSalesMap)){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

			}catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				try{
					if(dfr.defineBr != null){
						dfr.defineBr.close();
					}
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
			//---------------------------------------------------------------------------------
			//商品定義ファイルの読み込み及び保持
			//--------------------------------------------------------------------------------
			try{
				//商品定義ファイルを読み込むためメソッドを呼び出し
				dfr.DefinFilesReader(args[0], "commodity.lst", "商品", "^\\w{8}$", defineCoMap, coSalesMap);
				if(!dfr.DefinFilesReader(args[0], "commodity.lst", "商品", "^\\w{8}$", defineCoMap, coSalesMap)){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

			}catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				if(dfr.defineBr != null){
					dfr.defineBr.close();
				}
				if(dfr.defineBr != null){
					dfr.defineBr.close();
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
					if(!((fNumList.get(i) - i) == 1)){
						System.out.println("売上ファイル名が連番になっていません");
						return;
					}
				}
			}catch(Exception e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				try{
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
				for(int i = 0 ; i < fileNameList.size() ; i++){

					//ファイルパス指定して1ファイルずつ読み込み
					File salesFile = new File(args[0],fileList[i].getName());

					//売上ファイルの読み込み処理
					salesFr = new FileReader(salesFile);
					salesBr = new BufferedReader(salesFr);

					//1ファイル内のデータをすべてaddしていく
					while((salesLine = salesBr.readLine()) != null){
						salesList.add(salesLine);
					}

					//要素数のチェック→要素数が3以外はエラーを返す
					if((salesList.size() != 3)){
						System.out.println(fileList[i].getName() + "のフォーマットが不正です" );
						return;
					}
					/*すでに生成されている支店売上マップと商品売上マップと
					*読み込んできたファイル内のデータを比較して各コードが一致したら
					*それぞれのマップの値をインクリメントする
					 */
					//支店コードチェックを行う
					if(!(CalculateSales.brSalesMap.containsKey(salesList.get(0)))){
							System.out.println(fileList[i].getName() + "の支店コードが不正です");
							return;
					}
					//支店コードに紐づく売上金を加算する処理
					if(CalculateSales.brSalesMap.containsKey(salesList.get(0))){

						//支店売上金額をint型の計算用変数にキャストして格納
						long branchValue = Long.parseLong(salesList.get(2));
						branchValue += CalculateSales.brSalesMap.get(salesList.get(0));
						CalculateSales.brSalesMap.put(salesList.get(0), branchValue);

						//branchSalesMapに保持されている合計金額が10桁以上の場合エラーを返す
						if(String.valueOf(branchValue).length() > 10){
							System.out.println("合計金額が10桁を超えました");
							return;
						}
					}

					//商品コードが不正の場合、エラーメッセージ後に処理を終了
					if(!CalculateSales.coSalesMap.containsKey(salesList.get(1))){
						System.out.println(fileList[i].getName() + "の商品コードが不正です");
						return;
					}
					//商品コードに紐づく売上金を加算する処理
					if(CalculateSales.coSalesMap.containsKey(salesList.get(1))){

						//商品売上金額をint型の計算用変数にキャストして格納
						long commodValue = Long.parseLong(salesList.get(2));
						commodValue += CalculateSales.coSalesMap.get(salesList.get(1));
						CalculateSales.coSalesMap.put(salesList.get(1), commodValue);

						//commodSalesMapに保持されている合計金額が10桁以上の場合エラーを返す
						if(String.valueOf(commodValue).length() > 10){
							System.out.println("合計金額が10桁を超えました");
							return;
						}
					}
					//ArrayListの要素をクリア
					salesList.clear();
				}

			}catch(NumberFormatException e){
				e.printStackTrace();
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
			List<Map.Entry<String, Long>> branchSortEntries = new ArrayList<Map.Entry<String, Long>>(CalculateSales.brSalesMap.entrySet());
			Collections.sort(branchSortEntries, new Comparator<Map.Entry<String,Long>>() {
			//オーバーライド@Override		//アノテーション
				public int compare(
						Entry<String,Long> entry1, Entry<String,Long> entry2) {
	                return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
	            }
	        });
	        //商品売上金額のソート用のListを生成
			List<Map.Entry<String, Long>> commodSortEntries = new ArrayList<Map.Entry<String, Long>>(CalculateSales.coSalesMap.entrySet());
			Collections.sort(commodSortEntries, new Comparator<Map.Entry<String,Long>>() {
				 //オーバーライド
				@Override		//アノテーション
	            public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
	                return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
	            }
	        });

			/*System.out.println(branchSortEntries);
			System.out.println(commodSortEntries);*/

			//---------------------------------------------------------------------------------
			//集計結果を出力
			//---------------------------------------------------------------------------------

			try{

				//支店集計別ファイルを出力するメソッドを呼び出し
				dfr.SpreadSheetWriter(args[0], "branch.out", branchSortEntries, defineBrMap);
				if(!dfr.SpreadSheetWriter(args[0], "branch.out", branchSortEntries, defineBrMap)){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

				//商品集計別ファイルを出力するメソッドを呼び出し
				dfr.SpreadSheetWriter(args[0], "commodity.out", commodSortEntries, defineCoMap);
				if(!dfr.SpreadSheetWriter(args[0], "commodity.out", commodSortEntries, defineCoMap)){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

			}catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}

}
