import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEquipement } from '../equipement.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../equipement.test-samples';

import { EquipementService } from './equipement.service';

const requireRestSample: IEquipement = {
  ...sampleWithRequiredData,
};

describe('Equipement Service', () => {
  let service: EquipementService;
  let httpMock: HttpTestingController;
  let expectedResult: IEquipement | IEquipement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EquipementService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Equipement', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const equipement = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(equipement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Equipement', () => {
      const equipement = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(equipement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Equipement', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Equipement', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Equipement', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEquipementToCollectionIfMissing', () => {
      it('should add a Equipement to an empty array', () => {
        const equipement: IEquipement = sampleWithRequiredData;
        expectedResult = service.addEquipementToCollectionIfMissing([], equipement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipement);
      });

      it('should not add a Equipement to an array that contains it', () => {
        const equipement: IEquipement = sampleWithRequiredData;
        const equipementCollection: IEquipement[] = [
          {
            ...equipement,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, equipement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Equipement to an array that doesn't contain it", () => {
        const equipement: IEquipement = sampleWithRequiredData;
        const equipementCollection: IEquipement[] = [sampleWithPartialData];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, equipement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipement);
      });

      it('should add only unique Equipement to an array', () => {
        const equipementArray: IEquipement[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const equipementCollection: IEquipement[] = [sampleWithRequiredData];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, ...equipementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const equipement: IEquipement = sampleWithRequiredData;
        const equipement2: IEquipement = sampleWithPartialData;
        expectedResult = service.addEquipementToCollectionIfMissing([], equipement, equipement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipement);
        expect(expectedResult).toContain(equipement2);
      });

      it('should accept null and undefined values', () => {
        const equipement: IEquipement = sampleWithRequiredData;
        expectedResult = service.addEquipementToCollectionIfMissing([], null, equipement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipement);
      });

      it('should return initial array if no Equipement is added', () => {
        const equipementCollection: IEquipement[] = [sampleWithRequiredData];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, undefined, null);
        expect(expectedResult).toEqual(equipementCollection);
      });
    });

    describe('compareEquipement', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEquipement(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEquipement(entity1, entity2);
        const compareResult2 = service.compareEquipement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEquipement(entity1, entity2);
        const compareResult2 = service.compareEquipement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEquipement(entity1, entity2);
        const compareResult2 = service.compareEquipement(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
