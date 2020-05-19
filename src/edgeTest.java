import static com.codeborne.selenide.Selenide.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

public class edgeTest {
	@BeforeClass
	public static void beforeClass() {
		Logger beforeLogger = LoggerFactory.getLogger(edgeTest.class);
		beforeLogger.info("Edge版Selenide初期設定開始");
		Configuration.browser = WebDriverRunner.EDGE;
		System.setProperty("webdriver.edge.driver", "C:/Windows/System32/MicrosoftWebDriver.exe");
		beforeLogger.info("Edge版Selenide設定終了");
	}

	@Test
	public void openTest() {
		Logger testLogger = LoggerFactory.getLogger(edgeTest.class);
		testLogger.info("Edge版Selenide実行開始");
//		Selenide.open("https://www.google.com/?hl=ja");
//		Selenide.$("input[type=text]").val("test");
//		Selenide.open("file:\\\\\\C:\\Users\\gothi\\OneDrive\\ドキュメント\\alertSample.html");


		//画面の表示を最大化
		Configuration.startMaximized = true;

		//レポートの出力先
//		System.out.println(Configuration.reportsFolder);

		open("http://nave-kazu.hatenablog.com/entry/2016/12/12/171445");


		SelenideElement element = Selenide.$("#content-inner");
		WebDriver driver = element.getWrappedDriver();

        executeJavaScript("window.scrollTo(0, 0)");

        if(!(driver instanceof JavascriptExecutor)){
            throw new RuntimeException();
        }

        // 現在のウィンドウサイズを取得
        int windowWidth = Integer.parseInt(executeJavaScript("return document.documentElement.clientWidth;").toString());
        int windowHeight =Integer.parseInt(executeJavaScript("return document.documentElement.clientHeight;").toString());

        //キャプチャを取得する全体の画面サイズを取得
        int scrollWidth = Integer.parseInt(executeJavaScript("return document.documentElement.scrollWidth;").toString());
        int scrollHeight =Integer.parseInt(executeJavaScript("return document.documentElement.scrollHeight;").toString());

        //全体キャプチャ出力用のインスタンスを生成
        BufferedImage img = new BufferedImage(scrollWidth, scrollHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = img.getGraphics();

        // 画面最上部にスクロールする
        executeJavaScript("window.scrollTo(0, 0)");


        try{
            // 画面をスクロールさせながら、全体のスクリーンショットを取得
            int countVertical = 0;
            int countHorizontal = 0;
            int remainingHeight = scrollHeight;
            int remainingWidth = scrollWidth;
            BufferedImage imageParts;
            do{
                do{
                	//スクショファイルの格納先を確認するため一時的に作成
//                	File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                	//最後のスクロールで縦方向に重複する箇所をカット
                    if(remainingHeight < windowHeight * (countVertical +1)) {
                    	//最後のスクロールで横方向に重複する箇所をカット
                    	if(remainingWidth < windowWidth * (countHorizontal +1)) {
                    		int cutWidth = windowWidth * (countHorizontal +1) - remainingWidth;
                        	BufferedImage tmpImageParts = ImageIO.read(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
                        	imageParts = tmpImageParts.getSubimage(cutWidth, 0, (windowWidth - cutWidth), windowHeight);
                    	} else {
                        	int cutHeight = windowHeight * (countVertical +1) - remainingHeight;
                        	BufferedImage tmpImageParts = ImageIO.read(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
                        	imageParts = tmpImageParts.getSubimage(0, cutHeight, windowWidth, (windowHeight - cutHeight ));
                    	}

                    }else {
                    	imageParts = ImageIO.read(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
                    }
                	ImageIO.write(imageParts, "PNG", new File("C://Users/gothi/OneDrive/画像/スクリーンショット/samplePartsTmp" + windowWidth * countHorizontal + "_" + windowHeight * countVertical));

                    graphics.drawImage(imageParts, windowWidth * countHorizontal , windowHeight * countVertical, null);

                	ImageIO.write(img, "PNG", new File("C://Users/gothi/OneDrive/画像/スクリーンショット/sampleAllTmp" + windowWidth * countHorizontal + "_" + windowHeight * countVertical));
                    countHorizontal++;
                    executeJavaScript("window.scrollTo(arguments[0], arguments[1])",windowWidth * countHorizontal ,windowHeight * countVertical);
                }while(remainingWidth > windowWidth * countHorizontal);
                countHorizontal = 0;
                countVertical++;
                executeJavaScript("window.scrollTo(arguments[0], arguments[1])",0 ,windowHeight * countVertical);
            }while(remainingHeight > windowHeight * countVertical);

            ImageIO.write(img, "PNG", new File("C://Users/gothi/OneDrive/画像/スクリーンショット/sample"));

        }catch(IOException e){
        }
    }



//			if (isAlertPresent() == false) {
//				Selenide.$("#alertButton").click();
//				System.out.println("1回目");
//			}
//			if (isAlertPresent() == false) {
//				Selenide.$("#alertButton").click();
//			    System.out.println("2回目");
//			}
////			Alert alert = WebDriverRunner.getWebDriver().switchTo().alert();
////			alert.accept();
//			if (isAlertPresent() == false) {
//			    Selenide.screenshot("edgeTest");
//			    System.out.println("スクショ");
//			}
//			if (isAlertPresent() == false) {
//			    Selenide.$("#alertButton").click();
//			    System.out.println("3回目");
//			}
//		testLogger.info("Edge版Selenide実行終了");
//	}

	public boolean isAlertPresent() {
		try {
			Alert alert = WebDriverRunner.getWebDriver().switchTo().alert();
			return true;
		}catch(NoAlertPresentException nAPE) {
			return false;
		}
	}

}
