import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EquipementFormService } from './equipement-form.service';
import { EquipementService } from '../service/equipement.service';
import { IEquipement } from '../equipement.model';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';

import { EquipementUpdateComponent } from './equipement-update.component';

describe('Equipement Management Update Component', () => {
  let comp: EquipementUpdateComponent;
  let fixture: ComponentFixture<EquipementUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let equipementFormService: EquipementFormService;
  let equipementService: EquipementService;
  let parkService: ParkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EquipementUpdateComponent],
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
      .overrideTemplate(EquipementUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EquipementUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    equipementFormService = TestBed.inject(EquipementFormService);
    equipementService = TestBed.inject(EquipementService);
    parkService = TestBed.inject(ParkService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Park query and add missing value', () => {
      const equipement: IEquipement = { id: 456 };
      const park: IPark = { id: 94132 };
      equipement.park = park;

      const parkCollection: IPark[] = [{ id: 32403 }];
      jest.spyOn(parkService, 'query').mockReturnValue(of(new HttpResponse({ body: parkCollection })));
      const additionalParks = [park];
      const expectedCollection: IPark[] = [...additionalParks, ...parkCollection];
      jest.spyOn(parkService, 'addParkToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      expect(parkService.query).toHaveBeenCalled();
      expect(parkService.addParkToCollectionIfMissing).toHaveBeenCalledWith(
        parkCollection,
        ...additionalParks.map(expect.objectContaining)
      );
      expect(comp.parksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const equipement: IEquipement = { id: 456 };
      const park: IPark = { id: 62965 };
      equipement.park = park;

      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      expect(comp.parksSharedCollection).toContain(park);
      expect(comp.equipement).toEqual(equipement);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEquipement>>();
      const equipement = { id: 123 };
      jest.spyOn(equipementFormService, 'getEquipement').mockReturnValue(equipement);
      jest.spyOn(equipementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipement }));
      saveSubject.complete();

      // THEN
      expect(equipementFormService.getEquipement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(equipementService.update).toHaveBeenCalledWith(expect.objectContaining(equipement));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEquipement>>();
      const equipement = { id: 123 };
      jest.spyOn(equipementFormService, 'getEquipement').mockReturnValue({ id: null });
      jest.spyOn(equipementService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipement }));
      saveSubject.complete();

      // THEN
      expect(equipementFormService.getEquipement).toHaveBeenCalled();
      expect(equipementService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEquipement>>();
      const equipement = { id: 123 };
      jest.spyOn(equipementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(equipementService.update).toHaveBeenCalled();
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
  });
});
