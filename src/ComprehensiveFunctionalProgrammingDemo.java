import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;

/**
 * Comprehensive Java 17 Functional Programming Demonstration
 * Showcases all major functional programming features including:
 * - Lambda expressions
 * - Method references
 * - Functional interfaces (built-in and custom)
 * - Stream API operations (intermediate and terminal)
 * - Collectors
 * - Optional
 * - Pattern matching and text blocks
 * - Records (Java 17 feature)
 * - Sealed classes (Java 17 feature)
 */
public class ComprehensiveFunctionalProgrammingDemo {

    // ======================== RECORDS (Java 17 Feature) ========================
    public record Employee(int id, String name, String department, BigDecimal salary,
                           LocalDate hireDate, List<String> skills) {
        // Compact constructor with validation
        public Employee {
            Objects.requireNonNull(name, "Name cannot be null");
            Objects.requireNonNull(department, "Department cannot be null");
            if (salary.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Salary cannot be negative");
            }
            skills = List.copyOf(skills); // Defensive copy
        }

        // Additional methods
        public boolean hasSkill(String skill) {
            return skills.contains(skill);
        }

        public Employee withSalaryIncrease(BigDecimal percentage) {
            BigDecimal newSalary = salary.multiply(BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100))));
            return new Employee(id, name, department, newSalary, hireDate, skills);
        }
    }

    public record Department(String name, String location, int headCount) {
    }

    public record SalaryStatistics(BigDecimal min, BigDecimal max, BigDecimal average,
                                   long count, BigDecimal median) {
    }

    // ======================== SEALED CLASSES (Java 17 Feature) ========================
    public sealed interface Result<T> permits Success, Failure {
        static <T> Result<T> success(T value) {
            return new Success<>(value);
        }

        static <T> Result<T> failure(String error) {
            return new Failure<>(error);
        }
    }

    public record Success<T>(T value) implements Result<T> {
    }

    public record Failure<T>(String error) implements Result<T> {
    }

    // ======================== CUSTOM FUNCTIONAL INTERFACES ========================
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);

        // Default method
        default <S> TriFunction<T, U, V, S> andThen(Function<? super R, ? extends S> after) {
            Objects.requireNonNull(after);
            return (t, u, v) -> after.apply(apply(t, u, v));
        }
    }

    @FunctionalInterface
    public interface ExceptionHandler<T, R> {
        R apply(T input) throws Exception;

        static <T, R> Function<T, Optional<R>> lifting(ExceptionHandler<T, R> handler) {
            return input -> {
                try {
                    return Optional.of(handler.apply(input));
                } catch (Exception e) {
                    return Optional.empty();
                }
            };
        }
    }

    // ======================== SAMPLE DATA ========================
    private final List<Employee> employees = createSampleEmployees();
    private final Map<String, Department> departments = createSampleDepartments();

    private List<Employee> createSampleEmployees() {
        return List.of(
                new Employee(1, "Alice Johnson", "Engineering", new BigDecimal("85000"),
                        LocalDate.of(2020, 3, 15), List.of("Java", "Spring", "Microservices")),
                new Employee(2, "Bob Smith", "Engineering", new BigDecimal("92000"),
                        LocalDate.of(2019, 7, 22), List.of("Python", "Django", "AWS")),
                new Employee(3, "Carol Davis", "Marketing", new BigDecimal("68000"),
                        LocalDate.of(2021, 1, 10), List.of("SEO", "Content Marketing", "Analytics")),
                new Employee(4, "David Wilson", "Engineering", new BigDecimal("78000"),
                        LocalDate.of(2022, 5, 8), List.of("JavaScript", "React", "Node.js")),
                new Employee(5, "Eva Brown", "HR", new BigDecimal("72000"),
                        LocalDate.of(2020, 11, 3), List.of("Recruitment", "Training", "Policy")),
                new Employee(6, "Frank Miller", "Finance", new BigDecimal("89000"),
                        LocalDate.of(2018, 9, 12), List.of("Accounting", "Budgeting", "Excel")),
                new Employee(7, "Grace Lee", "Engineering", new BigDecimal("95000"),
                        LocalDate.of(2017, 4, 20), List.of("Java", "Kubernetes", "DevOps")),
                new Employee(8, "Henry Taylor", "Marketing", new BigDecimal("65000"),
                        LocalDate.of(2023, 2, 14), List.of("Social Media", "Brand Management"))
        );
    }

    private Map<String, Department> createSampleDepartments() {
        return Map.of(
                "Engineering", new Department("Engineering", "Building A", 4),
                "Marketing", new Department("Marketing", "Building B", 2),
                "HR", new Department("HR", "Building C", 1),
                "Finance", new Department("Finance", "Building A", 1)
        );
    }

    // ======================== LAMBDA EXPRESSIONS DEMONSTRATIONS ========================

    /**
     * Demonstrates various lambda expression syntaxes and use cases
     */
    public void demonstrateLambdaExpressions() {
        System.out.println("=== LAMBDA EXPRESSIONS DEMONSTRATION ===\n");

        // 1. Basic lambda with Runnable
        Runnable simpleTask = () -> System.out.println("Simple lambda without parameters");
        simpleTask.run();

        // 2. Lambda with single parameter
        Consumer<String> printer = message -> System.out.println("Message: " + message);
        printer.accept("Hello from lambda!");

        // 3. Lambda with multiple parameters
        BinaryOperator<Integer> multiplier = (a, b) -> a * b;
        System.out.println("Multiplication result: " + multiplier.apply(5, 3));

        // 4. Lambda with block body
        Function<String, String> formatter = text -> {
            if (text == null || text.isEmpty()) {
                return "Empty text";
            }
            return text.trim().toUpperCase();
        };
        System.out.println("Formatted: " + formatter.apply("  hello world  "));

        // 5. Lambda with exception handling
        Function<String, Integer> safeParser = text -> {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return 0;
            }
        };
        System.out.println("Parsed number: " + safeParser.apply("123"));
        System.out.println("Parsed invalid: " + safeParser.apply("abc"));

        // 6. Custom functional interface usage
        TriFunction<Integer, Integer, Integer, Integer> sumOfThree = (a, b, c) -> a + b + c;
        System.out.println("Sum of three: " + sumOfThree.apply(1, 2, 3));

        System.out.println();
    }

    // ======================== METHOD REFERENCES DEMONSTRATIONS ========================

    /**
     * Demonstrates all types of method references
     */
    public void demonstrateMethodReferences() {
        System.out.println("=== METHOD REFERENCES DEMONSTRATION ===\n");

        List<String> names = List.of("alice", "bob", "charlie", "diana");

        // 1. Static method reference
        Function<String, Integer> parseInt = Integer::parseInt;
        System.out.println("Parsed: " + parseInt.apply("42"));

        // 2. Instance method reference on a particular object
        String prefix = "Hello, ";
        Function<String, String> greeter = prefix::concat;
        System.out.println("Greeting: " + greeter.apply("World"));

        // 3. Instance method reference on arbitrary object
        names.stream()
                .map(String::toUpperCase)  // Instance method reference
                .forEach(System.out::println); // Static method reference

        // 4. Constructor reference
        Supplier<StringBuilder> sbSupplier = StringBuilder::new;
        Function<String, StringBuilder> sbFromString = StringBuilder::new;

        StringBuilder sb = sbSupplier.get();
        sb.append("Created with constructor reference");
        System.out.println(sb.toString());

        // 5. Array constructor reference
        IntFunction<String[]> arrayCreator = String[]::new;
        String[] array = arrayCreator.apply(5);
        System.out.println("Array length: " + array.length);

        System.out.println();
    }

    // ======================== BUILT-IN FUNCTIONAL INTERFACES ========================

    /**
     * Demonstrates usage of all major built-in functional interfaces
     */
    public void demonstrateBuiltInFunctionalInterfaces() {
        System.out.println("=== BUILT-IN FUNCTIONAL INTERFACES DEMONSTRATION ===\n");

        // Predicate<T> - boolean test(T t)
        Predicate<Employee> highEarner = emp -> emp.salary().compareTo(new BigDecimal("80000")) > 0;
        Predicate<Employee> engineeringDept = emp -> "Engineering".equals(emp.department());
        Predicate<Employee> combinedPredicate = highEarner.and(engineeringDept);

        System.out.println("High-earning engineers:");
        employees.stream()
                .filter(combinedPredicate)
                .forEach(emp -> System.out.println("  " + emp.name()));

        // Function<T, R> - R apply(T t)
        Function<Employee, String> nameFormatter = emp -> emp.name().toUpperCase();
        Function<String, Integer> lengthCalculator = String::length;
        Function<Employee, Integer> nameLength = nameFormatter.andThen(lengthCalculator);

        System.out.println("\nEmployee name lengths:");
        employees.forEach(emp ->
                System.out.println("  " + emp.name() + " -> " + nameLength.apply(emp)));

        // Consumer<T> - void accept(T t)
        Consumer<Employee> salaryReporter = emp ->
                System.out.println(emp.name() + " earns $" + emp.salary());
        Consumer<Employee> departmentReporter = emp ->
                System.out.println("  Department: " + emp.department());
        Consumer<Employee> combinedConsumer = salaryReporter.andThen(departmentReporter);

        System.out.println("\nEmployee details:");
        employees.stream().limit(2).forEach(combinedConsumer);

        // Supplier<T> - T get()
        Supplier<String> randomDepartment = () -> {
            String[] depts = {"Engineering", "Marketing", "HR", "Finance"};
            return depts[ThreadLocalRandom.current().nextInt(depts.length)];
        };
        System.out.println("\nRandom department: " + randomDepartment.get());

        // UnaryOperator<T> - T apply(T t)
        UnaryOperator<BigDecimal> salaryBonus = salary -> salary.multiply(new BigDecimal("1.1"));
        System.out.println("Salary with 10% bonus: $" + salaryBonus.apply(new BigDecimal("50000")));

        // BinaryOperator<T> - T apply(T t, T u)
        BinaryOperator<BigDecimal> salarySum = BigDecimal::add;
        BigDecimal totalSalary = employees.stream()
                .map(Employee::salary)
                .reduce(BigDecimal.ZERO, salarySum);
        System.out.println("Total salary budget: $" + totalSalary);

        // BiPredicate<T, U> - boolean test(T t, U u)
        BiPredicate<Employee, String> hasSkill = (emp, skill) -> emp.hasSkill(skill);
        System.out.println("\nEmployees with Java skill:");
        employees.stream()
                .filter(emp -> hasSkill.test(emp, "Java"))
                .forEach(emp -> System.out.println("  " + emp.name()));

        // BiFunction<T, U, R> - R apply(T t, U u)
        BiFunction<String, BigDecimal, Employee> createEmployee = (name, salary) ->
                new Employee(999, name, "Temp", salary, LocalDate.now(), List.of());
        Employee tempEmployee = createEmployee.apply("Temp Worker", new BigDecimal("45000"));
        System.out.println("Created temp employee: " + tempEmployee.name());

        // BiConsumer<T, U> - void accept(T t, U u)
        BiConsumer<String, BigDecimal> salaryAnnouncement = (name, salary) ->
                System.out.println(name + " has a salary of $" + salary);
        employees.forEach(emp -> salaryAnnouncement.accept(emp.name(), emp.salary()));

        System.out.println();
    }

    // ======================== STREAM API - CREATION METHODS ========================

    /**
     * Demonstrates various ways to create streams
     */
    public void demonstrateStreamCreation() {
        System.out.println("=== STREAM CREATION METHODS ===\n");

        // 1. From collections
        Stream<Employee> fromList = employees.stream();
        System.out.println("From list count: " + fromList.count());

        // 2. From arrays
        String[] skills = {"Java", "Python", "JavaScript", "Go"};
        Stream<String> fromArray = Arrays.stream(skills);
        System.out.println("From array: " + fromArray.collect(Collectors.joining(", ")));

        // 3. Stream.of()
        Stream<Integer> fromValues = Stream.of(1, 2, 3, 4, 5);
        System.out.println("From values sum: " + fromValues.mapToInt(Integer::intValue).sum());

        // 4. Stream.empty()
        Stream<String> emptyStream = Stream.empty();
        System.out.println("Empty stream count: " + emptyStream.count());

        // 5. Stream.generate() - infinite stream
        Stream<Double> randomNumbers = Stream.generate(Math::random).limit(5);
        System.out.println("Random numbers: " +
                randomNumbers.map(d -> String.format("%.2f", d)).collect(Collectors.joining(", ")));

        // 6. Stream.iterate() - infinite stream
        Stream<Integer> fibonacci = Stream.iterate(new int[]{0, 1},
                        arr -> new int[]{arr[1], arr[0] + arr[1]})
                .map(arr -> arr[0])
                .limit(10);
        System.out.println("Fibonacci: " + fibonacci.map(String::valueOf).collect(Collectors.joining(", ")));

        // 7. IntStream, LongStream, DoubleStream
        IntStream intRange = IntStream.range(1, 6);
        System.out.println("IntStream range sum: " + intRange.sum());

        // 8. Stream.builder()
        Stream<String> builderStream = Stream.<String>builder()
                .add("A")
                .add("B")
                .add("C")
                .build();
        System.out.println("Builder stream: " + builderStream.collect(Collectors.joining()));

        // 9. Parallel streams
        List<Integer> largeList = IntStream.range(1, 1000000).boxed().collect(Collectors.toList());
        long parallelSum = largeList.parallelStream().mapToLong(Integer::longValue).sum();
        System.out.println("Parallel stream sum: " + parallelSum);

        System.out.println();
    }

    // ======================== STREAM API - INTERMEDIATE OPERATIONS ========================

    /**
     * Demonstrates all intermediate operations
     */
    public void demonstrateIntermediateOperations() {
        System.out.println("=== STREAM INTERMEDIATE OPERATIONS ===\n");

        // 1. filter() - filters elements based on predicate
        System.out.println("1. FILTER - High earners (>$80k):");
        employees.stream()
                .filter(emp -> emp.salary().compareTo(new BigDecimal("80000")) > 0)
                .forEach(emp -> System.out.println("  " + emp.name() + " - $" + emp.salary()));

        // 2. map() - transforms elements
        System.out.println("\n2. MAP - Employee names in uppercase:");
        employees.stream()
                .map(Employee::name)
                .map(String::toUpperCase)
                .forEach(name -> System.out.println("  " + name));

        // 3. flatMap() - flattens nested structures
        System.out.println("\n3. FLATMAP - All unique skills:");
        employees.stream()
                .flatMap(emp -> emp.skills().stream())
                .distinct()
                .sorted()
                .forEach(skill -> System.out.println("  " + skill));

        // 4. distinct() - removes duplicates
        System.out.println("\n4. DISTINCT - Unique departments:");
        employees.stream()
                .map(Employee::department)
                .distinct()
                .forEach(dept -> System.out.println("  " + dept));

        // 5. sorted() - sorts elements
        System.out.println("\n5. SORTED - Employees by salary (descending):");
        employees.stream()
                .sorted(Comparator.comparing(Employee::salary).reversed())
                .forEach(emp -> System.out.println("  " + emp.name() + " - $" + emp.salary()));

        // 6. peek() - performs action without consuming
        System.out.println("\n6. PEEK - Processing log:");
        List<String> processedNames = employees.stream()
                .peek(emp -> System.out.println("  Processing: " + emp.name()))
                .map(Employee::name)
                .peek(name -> System.out.println("  Mapped to: " + name))
                .limit(3)
                .collect(Collectors.toList());

        // 7. limit() - limits number of elements
        System.out.println("\n7. LIMIT - First 3 employees:");
        employees.stream()
                .limit(3)
                .forEach(emp -> System.out.println("  " + emp.name()));

        // 8. skip() - skips first n elements
        System.out.println("\n8. SKIP - Skip first 2 employees:");
        employees.stream()
                .skip(2)
                .limit(3)
                .forEach(emp -> System.out.println("  " + emp.name()));

        // 9. takeWhile() - takes while condition is true (Java 9+)
        System.out.println("\n9. TAKEWHILE - Take while salary < $90k:");
        employees.stream()
                .sorted(Comparator.comparing(Employee::salary))
                .takeWhile(emp -> emp.salary().compareTo(new BigDecimal("90000")) < 0)
                .forEach(emp -> System.out.println("  " + emp.name() + " - $" + emp.salary()));

        // 10. dropWhile() - drops while condition is true (Java 9+)
        System.out.println("\n10. DROPWHILE - Drop while salary < $80k:");
        employees.stream()
                .sorted(Comparator.comparing(Employee::salary))
                .dropWhile(emp -> emp.salary().compareTo(new BigDecimal("80000")) < 0)
                .forEach(emp -> System.out.println("  " + emp.name() + " - $" + emp.salary()));

        // 11. mapToInt/Long/Double() - specialized mapping
        System.out.println("\n11. MAPTOINT - Salary statistics:");
        IntSummaryStatistics stats = employees.stream()
                .mapToInt(emp -> emp.salary().intValue())
                .summaryStatistics();
        System.out.println("  Average: $" + stats.getAverage());
        System.out.println("  Min: $" + stats.getMin());
        System.out.println("  Max: $" + stats.getMax());

        System.out.println();
    }

    // ======================== STREAM API - TERMINAL OPERATIONS ========================

    /**
     * Demonstrates all terminal operations
     */
    public void demonstrateTerminalOperations() {
        System.out.println("=== STREAM TERMINAL OPERATIONS ===\n");

        // 1. forEach() and forEachOrdered()
        System.out.println("1. FOREACH - All employee names:");
        employees.stream()
                .map(Employee::name)
                .forEach(name -> System.out.print(name + " "));
        System.out.println();

        System.out.println("\n   FOREACHORDERED - Parallel stream ordered:");
        employees.parallelStream()
                .map(Employee::name)
                .forEachOrdered(name -> System.out.print(name + " "));
        System.out.println();

        // 2. collect() - various collectors
        System.out.println("\n2. COLLECT operations:");

        // toList, toSet, toMap
        List<String> namesList = employees.stream().map(Employee::name).collect(Collectors.toList());
        Set<String> deptSet = employees.stream().map(Employee::department).collect(Collectors.toSet());
        Map<Integer, String> idToName = employees.stream()
                .collect(Collectors.toMap(Employee::id, Employee::name));

        System.out.println("  Names list size: " + namesList.size());
        System.out.println("  Departments set: " + deptSet);
        System.out.println("  ID to name map sample: " + idToName.get(1));

        // 3. reduce() - reduction operations
        System.out.println("\n3. REDUCE operations:");

        // Sum of salaries
        BigDecimal totalSalary = employees.stream()
                .map(Employee::salary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("  Total salary: $" + totalSalary);

        // Longest name
        Optional<String> longestName = employees.stream()
                .map(Employee::name)
                .reduce((name1, name2) -> name1.length() >= name2.length() ? name1 : name2);
        System.out.println("  Longest name: " + longestName.orElse("None"));

        // 4. count()
        long engineeringCount = employees.stream()
                .filter(emp -> "Engineering".equals(emp.department()))
                .count();
        System.out.println("\n4. COUNT - Engineering employees: " + engineeringCount);

        // 5. min() and max()
        System.out.println("\n5. MIN/MAX operations:");
        Optional<Employee> highestPaid = employees.stream()
                .max(Comparator.comparing(Employee::salary));
        Optional<Employee> lowestPaid = employees.stream()
                .min(Comparator.comparing(Employee::salary));

        highestPaid.ifPresent(emp ->
                System.out.println("  Highest paid: " + emp.name() + " - $" + emp.salary()));
        lowestPaid.ifPresent(emp ->
                System.out.println("  Lowest paid: " + emp.name() + " - $" + emp.salary()));

        // 6. findFirst() and findAny()
        System.out.println("\n6. FIND operations:");
        Optional<Employee> firstEngineer = employees.stream()
                .filter(emp -> "Engineering".equals(emp.department()))
                .findFirst();
        firstEngineer.ifPresent(emp -> System.out.println("  First engineer: " + emp.name()));

        Optional<Employee> anyMarketer = employees.stream()
                .filter(emp -> "Marketing".equals(emp.department()))
                .findAny();
        anyMarketer.ifPresent(emp -> System.out.println("  Any marketer: " + emp.name()));

        // 7. anyMatch(), allMatch(), noneMatch()
        System.out.println("\n7. MATCH operations:");
        boolean hasHighEarner = employees.stream()
                .anyMatch(emp -> emp.salary().compareTo(new BigDecimal("90000")) > 0);
        System.out.println("  Has high earner (>$90k): " + hasHighEarner);

        boolean allHaveSkills = employees.stream()
                .allMatch(emp -> !emp.skills().isEmpty());
        System.out.println("  All have skills: " + allHaveSkills);

        boolean noneFromSales = employees.stream()
                .noneMatch(emp -> "Sales".equals(emp.department()));
        System.out.println("  None from Sales: " + noneFromSales);

        // 8. toArray()
        System.out.println("\n8. TOARRAY:");
        String[] nameArray = employees.stream()
                .map(Employee::name)
                .toArray(String[]::new);
        System.out.println("  Names array length: " + nameArray.length);

        System.out.println();
    }

    // ======================== COLLECTORS DEMONSTRATION ========================

    /**
     * Demonstrates various Collector operations
     */
    public void demonstrateCollectors() {
        System.out.println("=== COLLECTORS DEMONSTRATION ===\n");

        // 1. Basic collectors
        System.out.println("1. BASIC COLLECTORS:");
        List<Employee> empList = employees.stream().collect(Collectors.toList());
        Set<String> deptSet = employees.stream().map(Employee::department).collect(Collectors.toSet());
        System.out.println("  Collected to list size: " + empList.size());
        System.out.println("  Unique departments: " + deptSet.size());

        // 2. joining()
        System.out.println("\n2. JOINING:");
        String allNames = employees.stream()
                .map(Employee::name)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("  All names: " + allNames);

        // 3. counting()
        Long totalCount = employees.stream().collect(Collectors.counting());
        System.out.println("\n3. COUNTING: " + totalCount);

        // 4. averaging, summing, summarizing
        System.out.println("\n4. NUMERIC OPERATIONS:");
        Double avgSalary = employees.stream()
                .collect(Collectors.averagingDouble(emp -> emp.salary().doubleValue()));
        System.out.println("  Average salary: $" + String.format("%.2f", avgSalary));

        BigDecimal totalSalarySum = employees.stream()
                .map(Employee::salary)
                .collect(Collectors.reducing(BigDecimal.ZERO, BigDecimal::add));
        System.out.println("  Total salary: $" + totalSalarySum);

        DoubleSummaryStatistics salaryStats = employees.stream()
                .collect(Collectors.summarizingDouble(emp -> emp.salary().doubleValue()));
        System.out.println("  Salary statistics: " + salaryStats);

        // 5. groupingBy()
        System.out.println("\n5. GROUPING BY:");
        Map<String, List<Employee>> byDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::department));
        byDepartment.forEach((dept, empList1) ->
                System.out.println("  " + dept + ": " + empList1.size() + " employees"));

        // Nested grouping
        Map<String, Map<Boolean, List<Employee>>> nestedGrouping = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.partitioningBy(emp -> emp.salary().compareTo(new BigDecimal("80000")) > 0)
                ));
        System.out.println("  Nested grouping sample: " +
                nestedGrouping.get("Engineering").get(true).size() + " high-paid engineers");

        // 6. partitioningBy()
        System.out.println("\n6. PARTITIONING BY:");
        Map<Boolean, List<Employee>> highLowEarners = employees.stream()
                .collect(Collectors.partitioningBy(emp ->
                        emp.salary().compareTo(new BigDecimal("80000")) > 0));
        System.out.println("  High earners: " + highLowEarners.get(true).size());
        System.out.println("  Lower earners: " + highLowEarners.get(false).size());

        // 7. mapping()
        System.out.println("\n7. MAPPING:");
        Map<String, List<String>> deptToNames = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.mapping(Employee::name, Collectors.toList())
                ));
        deptToNames.forEach((dept, names) ->
                System.out.println("  " + dept + " employees: " + String.join(", ", names)));

        // 8. filtering() (Java 9+)
        System.out.println("\n8. FILTERING:");
        Map<String, List<Employee>> highEarnersByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.filtering(emp -> emp.salary().compareTo(new BigDecimal("80000")) > 0,
                                Collectors.toList())
                ));
        highEarnersByDept.forEach((dept, highEarners) ->
                System.out.println("  " + dept + " high earners: " + highEarners.size()));

        // 9. Custom collector
        System.out.println("\n9. CUSTOM COLLECTOR:");
        SalaryStatistics customStats = employees.stream()
                .map(Employee::salary)
                .collect(createSalaryStatisticsCollector());
        System.out.println("  Custom salary stats: " + customStats);

        System.out.println();
    }

    // Custom collector for salary statistics
    private Collector<BigDecimal, ?, SalaryStatistics> createSalaryStatisticsCollector() {
        return Collector.of(
                ArrayList::new,  // supplier
                List::add,       // accumulator
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                }, // combiner
                list -> {        // finisher
                    if (list.isEmpty()) {
                        return new SalaryStatistics(BigDecimal.ZERO, BigDecimal.ZERO,
                                BigDecimal.ZERO, 0, BigDecimal.ZERO);
                    }

                    List<BigDecimal> sorted = list.stream().map(obj -> (BigDecimal) obj).sorted().collect(Collectors.toList());
                    BigDecimal min = sorted.get(0);
                    BigDecimal max = sorted.get(sorted.size() - 1);
                    BigDecimal sum = sorted.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal average = sum.divide(BigDecimal.valueOf(sorted.size()), 2, RoundingMode.HALF_UP);
                    BigDecimal median = sorted.size() % 2 == 0
                            ? sorted.get(sorted.size() / 2 - 1).add(sorted.get(sorted.size() / 2))
                            .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)
                            : sorted.get(sorted.size() / 2);

                    return new SalaryStatistics(min, max, average, sorted.size(), median);
                }
        );
    }

    // ======================== OPTIONAL DEMONSTRATION ========================

    /**
     * Demonstrates Optional usage patterns
     */
    public void demonstrateOptional() {
        System.out.println("=== OPTIONAL DEMONSTRATION ===\n");

        // 1. Creating Optional
        System.out.println("1. CREATING OPTIONAL:");
        Optional<String> empty = Optional.empty();
        Optional<String> nonEmpty = Optional.of("Hello");
        Optional<String> nullable = Optional.ofNullable(null);

        System.out.println("  Empty present: " + empty.isPresent());
        System.out.println("  NonEmpty present: " + nonEmpty.isPresent());
        System.out.println("  Nullable present: " + nullable.isPresent());

        // 2. Conditional actions
        System.out.println("\n2. CONDITIONAL ACTIONS:");
        findEmployeeById(1).ifPresent(emp ->
                System.out.println("  Found employee: " + emp.name()));

        findEmployeeById(999).ifPresentOrElse(
                emp -> System.out.println("  Found: " + emp.name()),
                () -> System.out.println("  Employee not found")
        );

        // 3. Transforming values
        System.out.println("\n3. TRANSFORMING VALUES:");
        Optional<String> upperCaseName = findEmployeeById(1)
                .map(Employee::name)
                .map(String::toUpperCase);
        System.out.println("  Uppercase name: " + upperCaseName.orElse("Unknown"));

        // 4. Filtering
        System.out.println("\n4. FILTERING:");
        Optional<Employee> highEarner = findEmployeeById(1)
                .filter(emp -> emp.salary().compareTo(new BigDecimal("80000")) > 0);
        System.out.println("  Is high earner: " + highEarner.isPresent());

        // 5. FlatMap for nested Optionals
        System.out.println("\n5. FLATMAP:");
        Optional<String> department = findEmployeeById(1)
                .flatMap(this::findDepartmentLocation);
        System.out.println("  Department location: " + department.orElse("Unknown"));

        // 6. Default values
        System.out.println("\n6. DEFAULT VALUES:");
        String name = findEmployeeById(999)
                .map(Employee::name)
                .orElse("Default Name");
        System.out.println("  Name with default: " + name);

        String nameFromSupplier = findEmployeeById(999)
                .map(Employee::name)
                .orElseGet(() -> "Generated at " + LocalDate.now());
        System.out.println("  Name from supplier: " + nameFromSupplier);

        // 7. Exception handling
        System.out.println("\n7. EXCEPTION HANDLING:");
        try {
            Employee emp = findEmployeeById(999)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
        } catch (RuntimeException e) {
            System.out.println("  Caught exception: " + e.getMessage());
        }

        // 8. Optional chaining
        System.out.println("\n8. OPTIONAL CHAINING:");
        String result = findEmployeeById(1)
                .filter(emp -> emp.salary().compareTo(new BigDecimal("80000")) > 0)
                .map(Employee::department)
                .map(String::toUpperCase)
                .orElse("NOT_APPLICABLE");
        System.out.println("  Chained result: " + result);

        System.out.println();
    }

    private Optional<Employee> findEmployeeById(int id) {
        return employees.stream()
                .filter(emp -> emp.id() == id)
                .findFirst();
    }

    private Optional<String> findDepartmentLocation(Employee employee) {
        return Optional.ofNullable(departments.get(employee.department()))
                .map(Department::location);
    }

    // ======================== PARALLEL STREAMS DEMONSTRATION ========================

    /**
     * Demonstrates parallel stream operations and considerations
     */
    public void demonstrateParallelStreams() {
        System.out.println("=== PARALLEL STREAMS DEMONSTRATION ===\n");

        // Create larger dataset for meaningful parallel processing
        List<Integer> largeDataset = IntStream.range(1, 1_000_000)
                .boxed()
                .collect(Collectors.toList());

        // 1. Sequential vs Parallel performance
        System.out.println("1. PERFORMANCE COMPARISON:");

        // Sequential processing
        long startTime = System.currentTimeMillis();
        long sequentialSum = largeDataset.stream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long sequentialTime = System.currentTimeMillis() - startTime;

        // Parallel processing
        startTime = System.currentTimeMillis();
        long parallelSum = largeDataset.parallelStream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long parallelTime = System.currentTimeMillis() - startTime;

        System.out.println("  Sequential sum: " + sequentialSum + " (Time: " + sequentialTime + "ms)");
        System.out.println("  Parallel sum: " + parallelSum + " (Time: " + parallelTime + "ms)");
        System.out.println("  Results match: " + (sequentialSum == parallelSum));

        // 2. Thread safety considerations
        System.out.println("\n2. THREAD SAFETY:");

        // Thread-safe collection
        Map<String, Long> threadSafeMap = new ConcurrentHashMap<>();
        employees.parallelStream()
                .forEach(emp -> threadSafeMap.merge(emp.department(), 1L, Long::sum));
        System.out.println("  Thread-safe department count: " + threadSafeMap);

        // 3. Parallel stream characteristics
        System.out.println("\n3. PARALLEL CHARACTERISTICS:");
        boolean isParallel = employees.parallelStream().isParallel();
        System.out.println("  Is parallel: " + isParallel);

        // Converting between sequential and parallel
        long count = employees.stream()
                .parallel()            // Convert to parallel
                .filter(emp -> emp.salary().compareTo(new BigDecimal("70000")) > 0)
                .sequential()          // Convert back to sequential
                .count();
        System.out.println("  Count after parallel/sequential conversion: " + count);

        // 4. Parallel reduce operations
        System.out.println("\n4. PARALLEL REDUCE:");

        Optional<BigDecimal> totalSalary = employees.parallelStream()
                .map(Employee::salary)
                .reduce(BigDecimal::add);
        System.out.println("  Total salary (parallel reduce): $" + totalSalary.orElse(BigDecimal.ZERO));

        // Custom parallel reduction
        BigDecimal totalWithIdentity = employees.parallelStream()
                .map(Employee::salary)
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add,        // accumulator
                        BigDecimal::add);       // combiner
        System.out.println("  Total with identity: $" + totalWithIdentity);

        System.out.println();
    }

    // ======================== ADVANCED FUNCTIONAL PROGRAMMING PATTERNS ========================

    /**
     * Demonstrates advanced functional programming patterns and techniques
     */
    public void demonstrateAdvancedPatterns() {
        System.out.println("=== ADVANCED FUNCTIONAL PROGRAMMING PATTERNS ===\n");

        // 1. Function composition
        System.out.println("1. FUNCTION COMPOSITION:");
        Function<String, String> removeSpaces = str -> str.replaceAll("\\s+", "");
        Function<String, String> toLowerCase = String::toLowerCase;
        Function<String, Integer> getLength = String::length;

        Function<String, Integer> composed = removeSpaces
                .andThen(toLowerCase)
                .andThen(getLength);

        System.out.println("  Composed function result: " + composed.apply("Hello World 123"));

        // 2. Currying simulation
        System.out.println("\n2. CURRYING:");
        Function<Integer, Function<Integer, Integer>> curriedAdd = x -> y -> x + y;
        Function<Integer, Integer> addFive = curriedAdd.apply(5);
        System.out.println("  Curried add 5 to 3: " + addFive.apply(3));

        // 3. Memoization
        System.out.println("\n3. MEMOIZATION:");
        Function<Integer, Long> memoizedFibonacci = memoize(this::fibonacci);
        System.out.println("  Memoized fibonacci(40): " + memoizedFibonacci.apply(40));
        System.out.println("  Memoized fibonacci(40) again: " + memoizedFibonacci.apply(40)); // Should be faster

        // 4. Either/Result pattern
        System.out.println("\n4. EITHER/RESULT PATTERN:");
        Result<Integer> successResult = parseInteger("123");
        Result<Integer> failureResult = parseInteger("abc");

        System.out.println("  Success result: " + handleResult(successResult));
        System.out.println("  Failure result: " + handleResult(failureResult));

        // 5. Monadic operations
        System.out.println("\n5. MONADIC OPERATIONS:");
        Optional<String> result = Optional.of("  hello world  ")
                .filter(s -> !s.trim().isEmpty())
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> s.length() > 5);
        System.out.println("  Monadic chain result: " + result.orElse("No result"));

        // 6. Exception lifting
        System.out.println("\n6. EXCEPTION LIFTING:");
        Function<String, Optional<Integer>> safeParser =
                ExceptionHandler.lifting(Integer::parseInt);

        System.out.println("  Safe parse '123': " + safeParser.apply("123"));
        System.out.println("  Safe parse 'abc': " + safeParser.apply("abc"));

        // 7. Lazy evaluation
        System.out.println("\n7. LAZY EVALUATION:");
        Supplier<String> expensiveComputation = () -> {
            System.out.println("  Performing expensive computation...");
            return "Computed result";
        };

        // Computation is not performed until get() is called
        System.out.println("  Lazy supplier created");
        System.out.println("  Result: " + expensiveComputation.get());

        // 8. Partial application
        System.out.println("\n8. PARTIAL APPLICATION:");
        TriFunction<String, String, String, String> formatMessage =
                (template, name, action) -> template.replace("{name}", name).replace("{action}", action);

        Function<String, Function<String, String>> partiallyApplied =
                name -> action -> formatMessage.apply("Hello {name}, you are {action}!", name, action);

        Function<String, String> greetAlice = partiallyApplied.apply("Alice");
        System.out.println("  Partial application: " + greetAlice.apply("coding"));

        System.out.println();
    }

    // Helper method for fibonacci calculation
    private long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    // Memoization helper
    private <T, R> Function<T, R> memoize(Function<T, R> function) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }

    // Result pattern helpers
    private Result<Integer> parseInteger(String str) {
        try {
            return Result.success(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Result.failure("Invalid number: " + str);
        }
    }

    private String handleResult(Result<Integer> result) {
        if (result instanceof Success<Integer> success) {
            return "Parsed successfully: " + success.value();
        } else if (result instanceof Failure<Integer> failure) {
            return "Parse failed: " + failure.error();
        } else {
            throw new IllegalArgumentException("Unknown result type");
        }
    }

    // ======================== REAL-WORLD APPLICATIONS ========================

    /**
     * Demonstrates real-world applications of functional programming
     */
    public void demonstrateRealWorldApplications() {
        System.out.println("=== REAL-WORLD APPLICATIONS ===\n");

        // 1. Data processing pipeline
        System.out.println("1. DATA PROCESSING PIPELINE:");
        Map<String, BigDecimal> departmentBudgets = employees.stream()
                .filter(emp -> emp.hireDate().isAfter(LocalDate.of(2019, 1, 1))) // Hired after 2019
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.mapping(Employee::salary,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        departmentBudgets.forEach((dept, budget) ->
                System.out.println("  " + dept + " budget (post-2019 hires): $" + budget));

        // 2. Configuration-driven processing
        System.out.println("\n2. CONFIGURATION-DRIVEN PROCESSING:");
        List<Predicate<Employee>> filters = List.of(
                emp -> emp.salary().compareTo(new BigDecimal("75000")) > 0,
                emp -> emp.skills().size() >= 2,
                emp -> "Engineering".equals(emp.department())
        );

        List<Employee> filteredEmployees = employees.stream()
                .filter(filters.stream().reduce(Predicate::and).orElse(emp -> true))
                .collect(Collectors.toList());

        System.out.println("  Employees matching all criteria: " + filteredEmployees.size());

        // 3. Reporting and analytics
        System.out.println("\n3. REPORTING AND ANALYTICS:");
        Map<String, DoubleSummaryStatistics> salaryStatsByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.summarizingDouble(emp -> emp.salary().doubleValue())
                ));

        salaryStatsByDept.forEach((dept, stats) ->
                System.out.println("  " + dept + " - Avg: $" + String.format("%.0f", stats.getAverage()) +
                        ", Range: $" + String.format("%.0f", stats.getMin()) +
                        "-$" + String.format("%.0f", stats.getMax())));

        // 4. Validation pipeline
        System.out.println("\n4. VALIDATION PIPELINE:");
        List<Function<Employee, Optional<String>>> validators = List.of(
                emp -> emp.name().length() < 2 ? Optional.of("Name too short") : Optional.empty(),
                emp -> emp.salary().compareTo(BigDecimal.ZERO) <= 0 ?
                        Optional.of("Invalid salary") : Optional.empty(),
                emp -> emp.skills().isEmpty() ? Optional.of("No skills listed") : Optional.empty()
        );

        Employee testEmployee = new Employee(999, "A", "Test", new BigDecimal("50000"),
                LocalDate.now(), List.of("Testing"));

        List<String> validationErrors = validators.stream()
                .map(validator -> validator.apply(testEmployee))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        System.out.println("  Validation errors for test employee: " + validationErrors);

        // 5. Event processing simulation
        System.out.println("\n5. EVENT PROCESSING:");
        List<String> events = List.of(
                "LOGIN:alice:2023-01-01T10:00:00",
                "PURCHASE:bob:2023-01-01T11:30:00",
                "LOGOUT:alice:2023-01-01T12:00:00",
                "LOGIN:charlie:2023-01-01T13:15:00"
        );

        Map<String, List<String>> eventsByType = events.stream()
                .collect(Collectors.groupingBy(event -> event.split(":")[0]));

        eventsByType.forEach((type, eventList) ->
                System.out.println("  " + type + " events: " + eventList.size()));

        // 6. Async processing simulation
        System.out.println("\n6. ASYNC PROCESSING SIMULATION:");
        List<CompletableFuture<String>> futures = employees.stream()
                .limit(3)
                .map(emp -> CompletableFuture.supplyAsync(() -> {
                    // Simulate async processing
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    return "Processed: " + emp.name();
                }))
                .collect(Collectors.toList());

        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        System.out.println("  Async processing results: " + results.size() + " completed");

        System.out.println();
    }

    // ======================== SPECIALIZED STREAM OPERATIONS ========================

    /**
     * Demonstrates specialized stream operations (primitive streams, etc.)
     */
    public void demonstrateSpecializedStreams() {
        System.out.println("=== SPECIALIZED STREAM OPERATIONS ===\n");

        // 1. IntStream operations
        System.out.println("1. INTSTREAM OPERATIONS:");
        IntStream.range(1, 10)
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        int sum = IntStream.of(1, 2, 3, 4, 5).sum();
        OptionalDouble average = IntStream.range(1, 101).average();
        System.out.println("  Sum: " + sum + ", Average 1-100: " + average.orElse(0.0));

        // 2. LongStream operations
        System.out.println("\n2. LONGSTREAM OPERATIONS:");
        long factorial = LongStream.rangeClosed(1, 10)
                .reduce(1, (a, b) -> a * b);
        System.out.println("  Factorial of 10: " + factorial);

        // 3. DoubleStream operations
        System.out.println("\n3. DOUBLESTREAM OPERATIONS:");
        double[] values = {1.1, 2.2, 3.3, 4.4, 5.5};
        DoubleSummaryStatistics stats = Arrays.stream(values).summaryStatistics();
        System.out.println("  Double stats: " + stats);

        // 4. Boxing and unboxing
        System.out.println("\n4. BOXING/UNBOXING:");
        List<Integer> boxedNumbers = IntStream.range(1, 6)
                .boxed()
                .collect(Collectors.toList());
        System.out.println("  Boxed numbers: " + boxedNumbers);

        int[] unboxedArray = boxedNumbers.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        System.out.println("  Unboxed array length: " + unboxedArray.length);

        // 5. Random number generation
        System.out.println("\n5. RANDOM NUMBER GENERATION:");
        List<Integer> randomInts = ThreadLocalRandom.current()
                .ints(5, 1, 100)
                .boxed()
                .collect(Collectors.toList());
        System.out.println("  Random integers: " + randomInts);

        // 6. File processing (simulation)
        System.out.println("\n6. TEXT PROCESSING:");
        String text = """
                This is a sample text for demonstration.
                It contains multiple lines and words.
                We will process it using streams.
                """;

        Map<Integer, Long> wordLengthFrequency = Pattern.compile("\\W+")
                .splitAsStream(text)
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(
                        String::length,
                        Collectors.counting()
                ));

        System.out.println("  Word length frequency: " + wordLengthFrequency);

        System.out.println();
    }

    // ======================== MAIN METHOD ========================

    /**
     * Main method to run all demonstrations
     */
    public static void main(String[] args) {
        ComprehensiveFunctionalProgrammingDemo demo = new ComprehensiveFunctionalProgrammingDemo();

        System.out.println("COMPREHENSIVE JAVA 17 FUNCTIONAL PROGRAMMING DEMONSTRATION");
        System.out.println("=".repeat(80) + "\n");

        // Run all demonstrations
        demo.demonstrateLambdaExpressions();
        demo.demonstrateMethodReferences();
        demo.demonstrateBuiltInFunctionalInterfaces();
        demo.demonstrateStreamCreation();
        demo.demonstrateIntermediateOperations();
        demo.demonstrateTerminalOperations();
        demo.demonstrateCollectors();
        demo.demonstrateOptional();
        demo.demonstrateParallelStreams();
        demo.demonstrateAdvancedPatterns();
        demo.demonstrateRealWorldApplications();
        demo.demonstrateSpecializedStreams();

        System.out.println("=".repeat(80));
        System.out.println("DEMONSTRATION COMPLETE - All Java 17 functional programming features covered!");
        System.out.println("Features demonstrated:");
        System.out.println("✓ Lambda expressions (all syntaxes)");
        System.out.println("✓ Method references (all types)");
        System.out.println("✓ Built-in functional interfaces");
        System.out.println("✓ Custom functional interfaces");
        System.out.println("✓ Stream creation methods");
        System.out.println("✓ All intermediate operations");
        System.out.println("✓ All terminal operations");
        System.out.println("✓ Collectors (all major types)");
        System.out.println("✓ Optional operations");
        System.out.println("✓ Parallel streams");
        System.out.println("✓ Advanced patterns (composition, currying, memoization)");
        System.out.println("✓ Real-world applications");
        System.out.println("✓ Specialized streams (IntStream, LongStream, DoubleStream)");
        System.out.println("✓ Java 17 features (Records, Sealed classes, Text blocks)");
    }
}

