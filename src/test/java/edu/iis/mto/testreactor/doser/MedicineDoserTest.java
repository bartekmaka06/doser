package edu.iis.mto.testreactor.doser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.iis.mto.testreactor.doser.infuser.Infuser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class MedicineDoserTest {
    @Mock
    private Infuser infuser;
    @Mock
    private Clock clock;
    @Mock
    private DosageLog dosageLog;
    private MedicineDoser medicineDoser;

    @BeforeEach
    void setUp() throws Exception{
        medicineDoser=new MedicineDoser(infuser,dosageLog,clock);
    }

    @Test
    void medicineDoserShouldHaveOne() {
        Medicine medicine = Medicine.of("Aspirin");
        CapacityUnit capacityUnit = CapacityUnit.MILILITER;
        Capacity capacity=Capacity.of(2,capacityUnit);
        TimeUnit timeUnit=TimeUnit.DAYS;
        Period period = Period.of(14,timeUnit);
        Dose dose = Dose.of(capacity,period);
        Receipe receipe = Receipe.of(medicine,dose,1);
        MedicinePackage medicinePackage = MedicinePackage.of(medicine,capacity);
        medicineDoser.add(medicinePackage);
        DosingResult result = medicineDoser.dose(receipe);
        assertEquals(DosingResult.SUCCESS, result);
    }
}
