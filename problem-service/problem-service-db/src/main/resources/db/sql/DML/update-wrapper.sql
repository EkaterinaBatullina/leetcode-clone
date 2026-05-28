UPDATE wrapper
SET wrapper =
        'import java.util.*;
        public class Main {
            __USER_CODE__
            public static void main(String[] args) {
                Scanner scanner = new Scanner(System.in);
                String inputLine = scanner.nextLine();
                String outputLine = scanner.nextLine();
                String[] inputCases = inputLine.split("\\\\n");
                String[] expectedOutputs = outputLine.split("\\\\n");
                if (inputCases.length != expectedOutputs.length) {
                    System.out.println("testcase count mismatch");
                    return;
                }
                for (int i = 0; i < inputCases.length; i++) {
                    String[] nums = inputCases[i].trim().split("\\s+");
                    int a = Integer.parseInt(nums[0]);
                    int b = Integer.parseInt(nums[1]);
                    int result = add(a, b);
                    int expected = Integer.parseInt(expectedOutputs[i].trim());
                    if (result == expected) {
                        System.out.println("test " + (i + 1) + " passed!");
                    } else {
                        System.out.println("test failed!" + (i + 1) + ": " + a + " + " + b + " = " + result + " (expected " + expected + ")");
                        return;
                    }
                }
                System.out.println("all tests passed!");
            }
        }'
WHERE problem_id = '<PROBLEM_UUID>' AND language_id = <LANGUAGE_ID>;
