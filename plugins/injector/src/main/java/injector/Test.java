package injector;

import runner.Plugin;
import runner.dto.ProcessedReportDTO;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) throws Exception {
        Plugin plugin = new DefectInjectorPlugin();
        HashMap<String,String> setting = new HashMap<>();
        setting.put("oil","periodic.oil");
        setting.put("defect","defect.json");
        setting.put("safe","safe.json");
        setting.put("trampoline","/home/hanjin/Downloads/trampoline");
        plugin.initialize("injector","/home/hanjin/.crichton/source/create",setting);
//        plugin.execute();
        ProcessedReportDTO DTO = plugin.transformReportData();
        System.out.println("!!!!!!!!!!!!!!!!");
    }
}
