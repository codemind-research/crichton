package runner.process;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class PythonProcessRunner extends ProcessRunner {

    private static final String PYTHON_VERSION_2 = "python";
    private static final String PYTHON_VERSION_3 = "python3";

    
    @Override
    protected String getProcessName() {
        // TODO: 추후 properties를 통해 Python 경로로 해서 반환 처리 해야함    
        boolean isPython2Installed = checkPython(PYTHON_VERSION_2);
        boolean isPython3Installed = checkPython(PYTHON_VERSION_3);

        if(isPython3Installed) {
            return PYTHON_VERSION_3;
        }
        else {
            return PYTHON_VERSION_2;
        }
    }


    private static boolean checkPython(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command, "--version");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Checking " + command + ":");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println(command + " is installed and working properly.\n");
                return true;
            } else {
                System.out.println(command + " is not installed or there was an issue.\n");
                return false;
            }


        } catch (Exception e) {
            System.out.println(command + " is not available on this system.");
            return false;
        }
    }
}
