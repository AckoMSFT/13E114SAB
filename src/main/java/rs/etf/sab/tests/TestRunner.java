package rs.etf.sab.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

public final class TestRunner {
    private static final int MAX_POINTS_ON_PUBLIC_TEST = 10;
    private static final Class[] UNIT_TEST_CLASSES = new Class[] {
            BuyerOperationsTest.class,
            CityOperationsTest.class,
            GeneralOperationsTest.class,
            ShopOperationsTest.class
    };

    private static final Class[] UNIT_TEST_CLASSES_PRIVATE = new Class[0];
    private static final Class[] MODULE_TEST_CLASSES = new Class[]{PublicModuleTest.class};
    private static final Class[] MODULE_TEST_CLASSES_PRIVATE = new Class[0];

    private static final Class[] TEST_CASES_ACKO = new Class[] {
            AckoTest.class
    };

    public TestRunner() {
    }

    private static double runUnitTestsPublic() {
        //int numberOfSuccessfulCases = false;
        //int numberOfAllCases = false;
        double points = 0.0;
        JUnitCore jUnitCore = new JUnitCore();
        Class[] var5 = UNIT_TEST_CLASSES;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Class testClass = var5[var7];
            System.out.println("\n" + testClass.getName());
            Request request = Request.aClass(testClass);
            Result result = jUnitCore.run(request);
            int numberOfAllCases = result.getRunCount();
            int numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }

            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            double points_curr = (double)numberOfSuccessfulCases * 6.0 / (double)numberOfAllCases / (double)UNIT_TEST_CLASSES.length;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }

        return points;
    }

    private static double runModuleTestsPublic() {
        //int numberOfSuccessfulCases = false;
        //int numberOfAllCases = false;
        double points = 0.0;
        JUnitCore jUnitCore = new JUnitCore();
        Class[] var5 = MODULE_TEST_CLASSES;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Class testClass = var5[var7];
            System.out.println("\n" + testClass.getName());
            Request request = Request.aClass(testClass);
            Result result = jUnitCore.run(request);
            int numberOfAllCases = result.getRunCount();
            int numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }

            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            double points_curr = (double)(numberOfSuccessfulCases / MODULE_TEST_CLASSES.length * 4);
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }

        return points;
    }

    private static double runPublic() {
        double res = 0.0;
        res += runUnitTestsPublic();
        res += runModuleTestsPublic();
        return res;
    }

    private static double runUnitTestsPrivate() {
        //int numberOfSuccessfulCases = false;
        //int numberOfAllCases = false;
        double points = 0.0;
        JUnitCore jUnitCore = new JUnitCore();
        Class[] var5 = UNIT_TEST_CLASSES_PRIVATE;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Class testClass = var5[var7];
            System.out.println("\n" + testClass.getName());
            Request request = Request.aClass(testClass);
            Result result = jUnitCore.run(request);
            int numberOfAllCases = result.getRunCount();
            int numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }

            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            double points_curr = (double)numberOfSuccessfulCases * 2.0 / (double)numberOfAllCases / (double)UNIT_TEST_CLASSES_PRIVATE.length;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }

        return points;
    }

    private static double runModuleTestsPrivate() {
        //int numberOfSuccessfulCases = false;
        //int numberOfAllCases = false;
        double points = 0.0;
        JUnitCore jUnitCore = new JUnitCore();
        Class[] var5 = MODULE_TEST_CLASSES_PRIVATE;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Class testClass = var5[var7];
            System.out.println("\n" + testClass.getName());
            Request request = Request.aClass(testClass);
            Result result = jUnitCore.run(request);
            int numberOfAllCases = result.getRunCount();
            int numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }

            System.out.println("Successful:" + numberOfSuccessfulCases);
            System.out.println("All:" + numberOfAllCases);
            double points_curr = (double)numberOfSuccessfulCases * 8.0 / (double)numberOfAllCases / (double)MODULE_TEST_CLASSES_PRIVATE.length;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }

        return points;
    }

    private static double runPrivate() {
        double res = 0.0;
        res += runUnitTestsPrivate();
        res += runModuleTestsPrivate();
        return res;
    }

    private static double runAckoTests() {
        double points = 0.0;
        JUnitCore jUnitCore = new JUnitCore();
        Class[] var5 = TEST_CASES_ACKO;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Class testClass = var5[var7];
            System.out.println("\n" + testClass.getName());
            Request request = Request.aClass(testClass);
            Result result = jUnitCore.run(request);
            int numberOfAllCases = result.getRunCount();
            int numberOfSuccessfulCases = result.getRunCount() - result.getFailureCount();
            if (numberOfSuccessfulCases < 0) {
                numberOfSuccessfulCases = 0;
            }

            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            double points_curr = (double)numberOfSuccessfulCases * 6.0 / (double)numberOfAllCases / (double)UNIT_TEST_CLASSES.length;
            System.out.println("Points: " + points_curr);
            points += points_curr;
        }

        return points;
    }
    private static double runAcko() {
        double res = 0.0;
        res += runAckoTests();
        return res;
    }
    public static void runTests() {
        double resultsAcko = runAcko();
        System.out.println("Points won on Acko's tests is: " + resultsAcko);

        double resultsPublic = runPublic();
        System.out.println("Points won on public tests is: " + resultsPublic + " out of 10");
    }
}
