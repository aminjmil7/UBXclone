import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPark } from '../park.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../park.test-samples';

import { ParkService, RestPark } from './park.service';

const requireRestSample: RestPark = {
  ...sampleWithRequiredData,
  dateInstall: sampleWithRequiredData.dateInstall?.toJSON(),
  dateOpen: sampleWithRequiredData.dateOpen?.toJSON(),
  dateClose: sampleWithRequiredData.dateClose?.toJSON(),
};

describe('Park Service', () => {
  let service: ParkService;
  let httpMock: HttpTestingController;
  let expectedResult: IPark | IPark[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ParkService);
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

    it('should create a Park', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const park = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(park).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Park', () => {
      const park = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(park).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Park', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Park', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Park', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addParkToCollectionIfMissing', () => {
      it('should add a Park to an empty array', () => {
        const park: IPark = sampleWithRequiredData;
        expectedResult = service.addParkToCollectionIfMissing([], park);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(park);
      });

      it('should not add a Park to an array that contains it', () => {
        const park: IPark = sampleWithRequiredData;
        const parkCollection: IPark[] = [
          {
            ...park,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addParkToCollectionIfMissing(parkCollection, park);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Park to an array that doesn't contain it", () => {
        const park: IPark = sampleWithRequiredData;
        const parkCollection: IPark[] = [sampleWithPartialData];
        expectedResult = service.addParkToCollectionIfMissing(parkCollection, park);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(park);
      });

      it('should add only unique Park to an array', () => {
        const parkArray: IPark[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const parkCollection: IPark[] = [sampleWithRequiredData];
        expectedResult = service.addParkToCollectionIfMissing(parkCollection, ...parkArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const park: IPark = sampleWithRequiredData;
        const park2: IPark = sampleWithPartialData;
        expectedResult = service.addParkToCollectionIfMissing([], park, park2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(park);
        expect(expectedResult).toContain(park2);
      });

      it('should accept null and undefined values', () => {
        const park: IPark = sampleWithRequiredData;
        expectedResult = service.addParkToCollectionIfMissing([], null, park, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(park);
      });

      it('should return initial array if no Park is added', () => {
        const parkCollection: IPark[] = [sampleWithRequiredData];
        expectedResult = service.addParkToCollectionIfMissing(parkCollection, undefined, null);
        expect(expectedResult).toEqual(parkCollection);
      });
    });

    describe('comparePark', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePark(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePark(entity1, entity2);
        const compareResult2 = service.comparePark(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePark(entity1, entity2);
        const compareResult2 = service.comparePark(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePark(entity1, entity2);
        const compareResult2 = service.comparePark(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
