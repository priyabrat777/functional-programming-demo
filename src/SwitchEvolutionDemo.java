/**
 * Comprehensive Switch Case Evolution Demo
 * Demonstrates the evolution of switch statements/expressions from Java 1.0 to Java 21
 * Base Java Version: Java 21 (CORRECTED VERSION)
 */
public class SwitchEvolutionDemo {

    // Enum for demonstration
    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // Record for demonstration (Java 14+)
    record Point(int x, int y) {}
    record Circle(Point center, int radius) {}
    record Rectangle(Point topLeft, int width, int height) {}

    // Sealed class hierarchy (Java 17+)
    sealed interface Shape permits CircleShape, RectangleShape, TriangleShape {}
    record CircleShape(Point center, int radius) implements Shape {}
    record RectangleShape(Point topLeft, int width, int height) implements Shape {}
    record TriangleShape(Point a, Point b, Point c) implements Shape {}

    public static void main(String[] args) {
        SwitchEvolutionDemo demo = new SwitchEvolutionDemo();

        System.out.println("=== Java Switch Case Evolution Demo ===\n");

        // 1. Traditional switch with primitives
        demo.traditionalSwitchWithPrimitives();

        // 2. Enhanced switch with strings (Java 7+)
        demo.switchWithStrings();

        // 3. Switch with enums (Java 5+)
        demo.switchWithEnums();

        // 4. Switch expressions (Java 14+)
        demo.switchExpressions();

        // 5. Enhanced switch expressions with yield (Java 14+)
        demo.switchExpressionsWithYield();

        // 6. Pattern matching with instanceof (Java 16+)
        demo.patternMatchingBasic();

        // 7. Enhanced pattern matching (Java 21)
        demo.enhancedPatternMatching();

        // 8. Null handling in switch (Java 21+)
        demo.nullHandlingInSwitch();

        // 9. Sealed classes with traditional switch (Java 21)
        demo.sealedClassesTraditionalSwitch();
    }

    /**
     * 1. Traditional Switch with Primitives (Java 1.0+)
     */
    public void traditionalSwitchWithPrimitives() {
        System.out.println("1. Traditional Switch with Primitives:");

        int dayNumber = 3;
        String dayName;

        switch (dayNumber) {
            case 1:
                dayName = "Monday";
                break;
            case 2:
                dayName = "Tuesday";
                break;
            case 3:
                dayName = "Wednesday";
                break;
            case 4:
                dayName = "Thursday";
                break;
            case 5:
                dayName = "Friday";
                break;
            case 6:
            case 7:
                dayName = "Weekend";
                break;
            default:
                dayName = "Invalid day";
                break;
        }

        System.out.println("Day " + dayNumber + " is: " + dayName);

        // Character switch
        char grade = 'B';
        switch (grade) {
            case 'A':
                System.out.println("Excellent!");
                break;
            case 'B':
                System.out.println("Good job!");
                break;
            case 'C':
                System.out.println("Average");
                break;
            default:
                System.out.println("Keep trying!");
        }
        System.out.println();
    }

    /**
     * 2. Switch with Strings (Java 7+)
     */
    public void switchWithStrings() {
        System.out.println("2. Switch with Strings (Java 7+):");

        String browser = "Chrome";
        String engineType;

        switch (browser) {
            case "Chrome":
            case "Edge":
                engineType = "Chromium-based";
                break;
            case "Firefox":
                engineType = "Gecko-based";
                break;
            case "Safari":
                engineType = "WebKit-based";
                break;
            default:
                engineType = "Unknown engine";
        }

        System.out.println(browser + " uses: " + engineType);
        System.out.println();
    }

    /**
     * 3. Switch with Enums (Java 5+)
     */
    public void switchWithEnums() {
        System.out.println("3. Switch with Enums (Java 5+):");

        Day today = Day.FRIDAY;
        boolean isWeekend;

        switch (today) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                isWeekend = false;
                break;
            case SATURDAY:
            case SUNDAY:
                isWeekend = true;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + today);
        }

        System.out.println(today + " is weekend: " + isWeekend);
        System.out.println();
    }

    /**
     * 4. Switch Expressions (Java 14+)
     */
    public void switchExpressions() {
        System.out.println("4. Switch Expressions (Java 14+):");

        Day today = Day.TUESDAY;

        // Switch expression with arrow syntax
        String dayType = switch (today) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
            case SATURDAY, SUNDAY -> "Weekend";
        };

        System.out.println(today + " is a: " + dayType);

        // Switch expression returning different types
        int numberOfLetters = switch (today) {
            case MONDAY, FRIDAY, SUNDAY -> 6;
            case TUESDAY -> 7;
            case WEDNESDAY -> 9;
            case THURSDAY, SATURDAY -> 8;
        };

        System.out.println(today + " has " + numberOfLetters + " letters");
        System.out.println();
    }

    /**
     * 5. Switch Expressions with Yield (Java 14+)
     */
    public void switchExpressionsWithYield() {
        System.out.println("5. Switch Expressions with Yield (Java 14+):");

        String month = "March";

        int daysInMonth = switch (month) {
            case "January", "March", "May", "July", "August", "October", "December" -> 31;
            case "April", "June", "September", "November" -> 30;
            case "February" -> {
                // Complex logic using yield
                System.out.println("Checking if leap year...");
                yield 28; // Simplified for demo
            }
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };

        System.out.println(month + " has " + daysInMonth + " days");
        System.out.println();
    }

    /**
     * 6. Pattern Matching with instanceof (Java 16+)
     */
    public void patternMatchingBasic() {
        System.out.println("6. Pattern Matching with instanceof (Java 16+):");

        Object obj = "Hello World";

        String result = switch (obj) {
            case String s -> "String of length: " + s.length();
            case Integer i -> "Integer with value: " + i;
            case Double d -> "Double with value: " + d;
            case null -> "Null value";
            default -> "Unknown type: " + obj.getClass().getSimpleName();
        };

        System.out.println("Result: " + result);
        System.out.println();
    }

    /**
     * 7. Enhanced Pattern Matching (Java 21)
     */
    public void enhancedPatternMatching() {
        System.out.println("7. Enhanced Pattern Matching (Java 21):");

        Object obj = 42;

        // Using traditional if-else with pattern matching for guard-like conditions
        String result = switch (obj) {
            case Integer i -> {
                if (i > 0) {
                    yield "Positive integer: " + i;
                } else if (i < 0) {
                    yield "Negative integer: " + i;
                } else {
                    yield "Zero";
                }
            }
            case String s -> {
                if (s.length() > 10) {
                    yield "Long string: " + s;
                } else {
                    yield "Short string: " + s;
                }
            }
            case null -> "Null value";
            default -> "Other type";
        };

        System.out.println("Enhanced pattern result: " + result);
        System.out.println();
    }

    /**
     * 8. Null Handling in Switch (Java 21+)
     */
    public void nullHandlingInSwitch() {
        System.out.println("8. Null Handling in Switch (Java 21+):");

        String value = null;

        // Null can be handled explicitly in switch
        String result = switch (value) {
            case null -> "Got null value";
            case "hello" -> "Got hello";
            case String s -> {
                if (s.isEmpty()) {
                    yield "Got empty string";
                } else {
                    yield "Got string: " + s;
                }
            }
        };

        System.out.println("Null handling result: " + result);

        // Combining null with other patterns
        Object mixed = null;
        String mixedResult = switch (mixed) {
            case null -> "Null object";
            case String s -> {
                if (s.length() > 5) {
                    yield "Long string: " + s;
                } else {
                    yield "Short string: " + s;
                }
            }
            case Integer i -> "Integer: " + i;
            default -> "Something else";
        };

        System.out.println("Mixed null handling: " + mixedResult);
        System.out.println();
    }

    /**
     * 9. Sealed Classes with Traditional Switch (Java 21)
     */
    public void sealedClassesTraditionalSwitch() {
        System.out.println("9. Sealed Classes with Traditional Switch (Java 21):");

        Shape shape = new CircleShape(new Point(0, 0), 5);

        // Using traditional approach since pattern matching with records is preview
        double area = switch (shape) {
            case CircleShape circle -> Math.PI * circle.radius() * circle.radius();
            case RectangleShape rectangle -> rectangle.width() * rectangle.height();
            case TriangleShape triangle -> {
                // Simplified triangle area calculation using Heron's formula approximation
                Point a = triangle.a();
                Point b = triangle.b();
                Point c = triangle.c();
                yield 0.5 * Math.abs((a.x() * (b.y() - c.y()) +
                        b.x() * (c.y() - a.y()) +
                        c.x() * (a.y() - b.y())));
            }
        };

        System.out.println("Shape area: " + area);
        System.out.println();

        System.out.println("=== Demo Complete ===");
    }
}
