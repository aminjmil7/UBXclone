import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RatingFormService } from './rating-form.service';
import { RatingService } from '../service/rating.service';
import { IRating } from '../rating.model';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

import { RatingUpdateComponent } from './rating-update.component';

describe('Rating Management Update Component', () => {
  let comp: RatingUpdateComponent;
  let fixture: ComponentFixture<RatingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ratingFormService: RatingFormService;
  let ratingService: RatingService;
  let parkService: ParkService;
  let equipementService: EquipementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RatingUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RatingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RatingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ratingFormService = TestBed.inject(RatingFormService);
    ratingService = TestBed.inject(RatingService);
    parkService = TestBed.inject(ParkService);
    equipementService = TestBed.inject(EquipementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Park query and add missing value', () => {
      const rating: IRating = { id: 456 };
      const park: IPark = { id: 16189 };
      rating.park = park;

      const parkCollection: IPark[] = [{ id: 8329 }];
      jest.spyOn(parkService, 'query').mockReturnValue(of(new HttpResponse({ body: parkCollection })));
      const additionalParks = [park];
      const expectedCollection: IPark[] = [...additionalParks, ...parkCollection];
      jest.spyOn(parkService, 'addParkToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rating });
      comp.ngOnInit();

      expect(parkService.query).toHaveBeenCalled();
      expect(parkService.addParkToCollectionIfMissing).toHaveBeenCalledWith(
        parkCollection,
        ...additionalParks.map(expect.objectContaining)
      );
      expect(comp.parksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Equipement query and add missing value', () => {
      const rating: IRating = { id: 456 };
      const equipement: IEquipement = { id: 35059 };
      rating.equipement = equipement;

      const equipementCollection: IEquipement[] = [{ id: 45052 }];
      jest.spyOn(equipementService, 'query').mockReturnValue(of(new HttpResponse({ body: equipementCollection })));
      const additionalEquipements = [equipement];
      const expectedCollection: IEquipement[] = [...additionalEquipements, ...equipementCollection];
      jest.spyOn(equipementService, 'addEquipementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rating });
      comp.ngOnInit();

      expect(equipementService.query).toHaveBeenCalled();
      expect(equipementService.addEquipementToCollectionIfMissing).toHaveBeenCalledWith(
        equipementCollection,
        ...additionalEquipements.map(expect.objectContaining)
      );
      expect(comp.equipementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const rating: IRating = { id: 456 };
      const park: IPark = { id: 67956 };
      rating.park = park;
      const equipement: IEquipement = { id: 78699 };
      rating.equipement = equipement;

      activatedRoute.data = of({ rating });
      comp.ngOnInit();

      expect(comp.parksSharedCollection).toContain(park);
      expect(comp.equipementsSharedCollection).toContain(equipement);
      expect(comp.rating).toEqual(rating);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRating>>();
      const rating = { id: 123 };
      jest.spyOn(ratingFormService, 'getRating').mockReturnValue(rating);
      jest.spyOn(ratingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rating });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: rating }));
      saveSubject.complete();

      // THEN
      expect(ratingFormService.getRating).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ratingService.update).toHaveBeenCalledWith(expect.objectContaining(rating));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRating>>();
      const rating = { id: 123 };
      jest.spyOn(ratingFormService, 'getRating').mockReturnValue({ id: null });
      jest.spyOn(ratingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rating: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: rating }));
      saveSubject.complete();

      // THEN
      expect(ratingFormService.getRating).toHaveBeenCalled();
      expect(ratingService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRating>>();
      const rating = { id: 123 };
      jest.spyOn(ratingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rating });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ratingService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePark', () => {
      it('Should forward to parkService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(parkService, 'comparePark');
        comp.comparePark(entity, entity2);
        expect(parkService.comparePark).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEquipement', () => {
      it('Should forward to equipementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(equipementService, 'compareEquipement');
        comp.compareEquipement(entity, entity2);
        expect(equipementService.compareEquipement).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
