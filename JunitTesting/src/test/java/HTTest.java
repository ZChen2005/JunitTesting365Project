import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class HTTest {

    private HT ht;

    @BeforeEach
    void setUp() {
        ht = new HT();
    }

    @AfterEach
    void tearDown() {
        ht = null;
    }

   
    @Test
    void testGetTotalWordCount_Empty() {
        assertEquals(0, ht.getTotalWordCount());
    }

   
    @Test
    void testGetTotalWordCount_SingleWord() {
        ht.add("apple");
        assertEquals(1, ht.getTotalWordCount());
    }

    
    @Test
    void testGetTotalWordCount_MultipleCounts() {
        ht.add("apple");
        ht.add("apple");
        ht.add("banana");
        assertEquals(3, ht.getTotalWordCount());
    }

    
    @Test
    void testGetTotalWordCount_Large() {
        for (int i = 0; i < 1000; i++) ht.add("word");
        assertEquals(1000, ht.getTotalWordCount());
    }

    
    @Test
    void testResize_DoublesCapacity() {
        int original = ht.table.length;
        ht.resizeV2();
        assertEquals(original * 2, ht.table.length);
    }

   
    @Test
    void testResize_PreservesElements() {
        ht.add("apple");
        ht.add("banana");

        ht.resizeV2();

        assertTrue(ht.contains("apple"));
        assertTrue(ht.contains("banana"));
    }

    
    @Test
    void testResize_ManyElements() {
        for (int i = 0; i < 50; i++) {
            ht.add("w" + i);
        }

        ht.resizeV2();

        for (int i = 0; i < 50; i++) {
            assertTrue(ht.contains("w" + i));
        }
    }
}
