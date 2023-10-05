
package com.epsonconnectjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class FibonacciTest {

    @Test
    public void testCalculate() {
        assertEquals(0, Fibonacci.calculate(0));
        assertEquals(1, Fibonacci.calculate(1));
        assertEquals(1, Fibonacci.calculate(2));
        assertEquals(2, Fibonacci.calculate(3));
        assertEquals(3, Fibonacci.calculate(4));
        assertEquals(5, Fibonacci.calculate(5));
    }
}
