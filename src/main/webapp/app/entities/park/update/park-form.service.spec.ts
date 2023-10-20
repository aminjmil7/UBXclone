import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../park.test-samples';

import { ParkFormService } from './park-form.service';

describe('Park Form Service', () => {
  let service: ParkFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ParkFormService);
  });

  describe('Service methods', () => {
    describe('createParkFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createParkFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            parkName: expect.any(Object),
            parkAddress: expect.any(Object),
            longtitude: expect.any(Object),
            latitude: expect.any(Object),
            verified: expect.any(Object),
            dateInstall: expect.any(Object),
            dateOpen: expect.any(Object),
            dateClose: expect.any(Object),
            note: expect.any(Object),
            reseller: expect.any(Object),
          })
        );
      });

      it('passing IPark should create a new form with FormGroup', () => {
        const formGroup = service.createParkFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            parkName: expect.any(Object),
            parkAddress: expect.any(Object),
            longtitude: expect.any(Object),
            latitude: expect.any(Object),
            verified: expect.any(Object),
            dateInstall: expect.any(Object),
            dateOpen: expect.any(Object),
            dateClose: expect.any(Object),
            note: expect.any(Object),
            reseller: expect.any(Object),
          })
        );
      });
    });

    describe('getPark', () => {
      it('should return NewPark for default Park initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createParkFormGroup(sampleWithNewData);

        const park = service.getPark(formGroup) as any;

        expect(park).toMatchObject(sampleWithNewData);
      });

      it('should return NewPark for empty Park initial value', () => {
        const formGroup = service.createParkFormGroup();

        const park = service.getPark(formGroup) as any;

        expect(park).toMatchObject({});
      });

      it('should return IPark', () => {
        const formGroup = service.createParkFormGroup(sampleWithRequiredData);

        const park = service.getPark(formGroup) as any;

        expect(park).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPark should not enable id FormControl', () => {
        const formGroup = service.createParkFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPark should disable id FormControl', () => {
        const formGroup = service.createParkFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
