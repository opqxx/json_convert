import com.thunisoft.sjzh.utils.ConvertUtil;
import com.thunisoft.sjzh.utils.FileUtils;

public class Test {

    public static void main(String[] args){
        String targetJson = FileUtils.getResourcesText("/data/test2.json");
        String configJson = FileUtils.getResourcesText("/template/sjzh_test2.json");

        String result = ConvertUtil.convertData(targetJson, configJson,true);

        System.out.println(result);
    }
}
