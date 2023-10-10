package com.example.cocktailmachine.data.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author Johanna Reidt
 * @created Di. 10.Okt 2023 - 15:15
 * @project CocktailMachine
 */
class CalibrateStatusTest {
    /**
     * Kalibrierung
     * <p>
     * ready: Maschine ist bereit für eine Kalibrierung
     * calibration empty container: Kalibrierung wartet auf ein leeres Gefäß. Es sollte calibration_add_empty ausgeführt werden.
     * calibration known weight: Kalibrierung wartet auf ein Gewicht. Es sollte calibration_add_weight ausgeführt werden.
     * calibration pumps: Kalibrierung pumpt Flüssigkeiten
     * calibration calculation: Kalibrierung berechnet die Werte
     * calibration done: Kalibrierung fertig. Es sollte calibration_finish ausgeführt werden.
     */

    @Test
    void setStatus() {
        CalibrateStatus.setStatus("jdfkjh");
        assertEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("ready");
        assertEquals(CalibrateStatus.ready, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration empty container");
        assertEquals(CalibrateStatus.calibration_empty_container, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration empty container ");
        assertEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration known weight");
        assertEquals(CalibrateStatus.calibration_known_weight, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration known weight ");
        assertEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration pumps");
        assertEquals(CalibrateStatus.calibration_pumps, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("ca libration pumps");
        assertEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration calculation");
        assertEquals(CalibrateStatus.calibration_calculation, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration calculat ion");
        assertEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibration done");
        assertEquals(CalibrateStatus.calibration_done, CalibrateStatus.getCurrent());
        CalibrateStatus.setStatus("calibrat ion done");
        assertEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());
    }

    @Test
    void testSetStatus() {
        CalibrateStatus.setStatus(CalibrateStatus.not);
        assertEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());
        assertNotEquals(CalibrateStatus.calibration_done, CalibrateStatus.getCurrent());

        CalibrateStatus.setStatus(CalibrateStatus.ready);
        assertEquals(CalibrateStatus.ready, CalibrateStatus.getCurrent());
        assertNotEquals(CalibrateStatus.not, CalibrateStatus.getCurrent());

        CalibrateStatus.setStatus(CalibrateStatus.calibration_empty_container);
        assertEquals(CalibrateStatus.calibration_empty_container, CalibrateStatus.getCurrent());
        assertNotEquals(CalibrateStatus.calibration_known_weight, CalibrateStatus.getCurrent());

        CalibrateStatus.setStatus(CalibrateStatus.calibration_known_weight);
        assertEquals(CalibrateStatus.calibration_known_weight, CalibrateStatus.getCurrent());
        assertNotEquals(CalibrateStatus.calibration_empty_container, CalibrateStatus.getCurrent());

        CalibrateStatus.setStatus(CalibrateStatus.calibration_pumps);
        assertEquals(CalibrateStatus.calibration_pumps, CalibrateStatus.getCurrent());
        assertNotEquals(CalibrateStatus.calibration_calculation, CalibrateStatus.getCurrent());

        CalibrateStatus.setStatus(CalibrateStatus.calibration_calculation);
        assertEquals(CalibrateStatus.calibration_calculation, CalibrateStatus.getCurrent());
        assertNotEquals(CalibrateStatus.calibration_pumps, CalibrateStatus.getCurrent());

        CalibrateStatus.setStatus(CalibrateStatus.calibration_done);
        assertEquals(CalibrateStatus.calibration_done, CalibrateStatus.getCurrent());
        assertNotEquals(CalibrateStatus.ready, CalibrateStatus.getCurrent());
    }

    @Test
    void testToString() {
    }

    @Test
    void valueStringOf() {
    }

    @Test
    void getCurrent() {
    }

    @Test
    void testGetCurrent() {
    }

    @Test
    void testGetCurrent1() {
    }

    @Test
    void values() {
    }

    @Test
    void valueOf() {
    }
}