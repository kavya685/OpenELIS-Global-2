package org.openelisglobal.patient.daoimpl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.openelisglobal.BaseWebContextSensitiveTest;
import org.openelisglobal.patient.dao.PatientDAO;
import org.openelisglobal.patient.valueholder.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

public class PatientDAOTest extends BaseWebContextSensitiveTest {

    @Autowired
    private PatientDAO patientDAO;

    // JUnit 5 uses @BeforeEach instead of @Before
    @BeforeEach
    void setUp() throws Exception {
        // Setup any shared data here if needed
    }

    /**
     * Requirement: Implement edge-case for non-existent UUIDs/IDs.
     */
    @Test
    void testGetPatientByNonExistentId_ReturnsNull() {
        Patient result = patientDAO.getData("NON_EXISTENT_ID_999");
        assertNull(result, "Should return null when searching for a non-existent ID");
    }

    /**
     * Requirement: Handle special characters in names/IDs.
     */
    @Test
    void testGetPatientByNationalId_WithSpecialCharacters() {
        String specialId = "ID-123'OR'1=1-!!"; // Testing SQL injection safety/special chars
        Patient patient = new Patient();
        patient.setNationalId(specialId);

        // Note: You may need to create a Person first depending on DB constraints
        patientDAO.insert(patient);

        Patient result = patientDAO.getPatientByNationalId(specialId);
        assertNotNull(result);
        assertEquals(specialId, result.getNationalId());
    }

    /**
     * Requirement: Verify Hibernate constraints for invalid data.
     */
    @Test
    void testInsertPatient_ViolatingConstraints_ThrowsException() {
        Patient invalidPatient = new Patient();
        // Purposefully leaving required fields null to trigger Hibernate validation

        assertThrows(Exception.class, () -> {
            patientDAO.insert(invalidPatient);
        }, "Should throw an exception when database constraints are violated");
    }
}