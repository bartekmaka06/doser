package edu.iis.mto.testreactor.doser;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.iis.mto.testreactor.doser.infuser.Infuser;
import edu.iis.mto.testreactor.doser.infuser.InfuserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.*;
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
    private Dose dose;
    private Receipe receipe ;
    private Medicine medicine;


    @BeforeEach
    void setUp(){
        medicineDoser=new MedicineDoser(infuser,dosageLog,clock);
        medicine = Medicine.of("Aspirin");
        CapacityUnit capacityUnit = CapacityUnit.MILILITER;
        Capacity capacity=Capacity.of(2,capacityUnit);
        TimeUnit timeUnit=TimeUnit.DAYS;
        Period period = Period.of(14,timeUnit);
        dose = Dose.of(capacity,period);
        receipe = Receipe.of(medicine,dose,1);
        MedicinePackage medicinePackage = MedicinePackage.of(medicine, capacity);
        medicineDoser.add(medicinePackage);
    }

    @Test
    void medicineDoserShouldReturnDoseWithSuccess() {
        DosingResult result = medicineDoser.dose(receipe);
        assertEquals(DosingResult.SUCCESS, result);
    }

    @Test
    void medicineDoserInDoseMethodShouldReturnUnavailableMedicineException() throws InfuserException {
        Medicine medicine = Medicine.of("NULL");
        receipe = Receipe.of(medicine,dose,1);
        assertThrows(UnavailableMedicineException.class,()->medicineDoser.dose(receipe));
    }

    @Test
    void medicineDoserInDoseMethodShouldReturnInsufficientMedicineException(){
        receipe = Receipe.of(medicine,dose,3);
        assertThrows(InsufficientMedicineException.class,()->medicineDoser.dose(receipe));
    }

    @Test
    void medicineDoserInDoseMethodShouldCallLogDifuserErrorWhenItReturnException() throws InfuserException {
        doThrow(InfuserException.class).when(infuser).dispense(any(MedicinePackage.class),any(Capacity.class));
        medicineDoser.dose(receipe);
        verify(dosageLog,times(1)).logDifuserError(any(),any());
    }

    @Test
    void medicineDoserInDoseMethodShouldCallLogStartandLogEndTheSameNumberOfTimes(){
        medicineDoser.dose(receipe);
        verify(dosageLog,times(1)).logStart();
        verify(dosageLog,times(1)).logEnd();
    }
}
