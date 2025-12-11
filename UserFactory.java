package exp;

public final class UserFactory {
    private static int stuSeq = 0;
    private static int runSeq = 0;

    private UserFactory() {}

    public static Student createStudent(String name, String phone, String major) {
        return new Student("S" + (++stuSeq), name, phone, major);
    }

    public static Runner createRunner(String name, String phone) {
        return new Runner(name, phone);
    }
}