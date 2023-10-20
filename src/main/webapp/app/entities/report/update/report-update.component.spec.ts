import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReportFormService } from './report-form.service';
import { ReportService } from '../service/report.service';
import { IReport } from '../report.model';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';

import { ReportUpdateComponent } from './report-update.component';

describe('Report Management Update Component', () => {
  let comp: ReportUpdateComponent;
  let fixture: ComponentFixture<ReportUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reportFormService: ReportFormService;
  let reportService: ReportService;
  let equipementService: EquipementService;
  let parkService: ParkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ReportUpdateComponent],
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
      .overrideTemplate(ReportUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReportUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reportFormService = TestBed.inject(ReportFormService);
    reportService = TestBed.inject(ReportService);
    equipementService = TestBed.inject(EquipementService);
    parkService = TestBed.inject(ParkService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Equipement query and add missing value', () => {
      const report: IReport = { id: 456 };
      const equipement: IEquipement = { id: 77765 };
      report.equipement = equipement;

      const equipementCollection: IEquipement[] = [{ id: 45780 }];
      jest.spyOn(equipementService, 'query').mockReturnValue(of(new HttpResponse({ body: equipementCollection })));
      const additionalEquipements = [equipement];
      const expectedCollection: IEquipement[] = [...additionalEquipements, ...equipementCollection];
      jest.spyOn(equipementService, 'addEquipementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ report });
      comp.ngOnInit();

      expect(equipementService.query).toHaveBeenCalled();
      expect(equipementService.addEquipementToCollectionIfMissing).toHaveBeenCalledWith(
        equipementCollection,
        ...additionalEquipements.map(expect.objectContaining)
      );
      expect(comp.equipementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Park query and add missing value', () => {
      const report: IReport = { id: 456 };
      const park: IPark = { id: 51653 };
      report.park = park;

      const parkCollection: IPark[] = [{ id: 72826 }];
      jest.spyOn(parkService, 'query').mockReturnValue(of(new HttpResponse({ body: parkCollection })));
      const additionalParks = [park];
      const expectedCollection: IPark[] = [...additionalParks, ...parkCollection];
      jest.spyOn(parkService, 'addParkToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ report });
      comp.ngOnInit();

      expect(parkService.query).toHaveBeenCalled();
      expect(parkService.addParkToCollectionIfMissing).toHaveBeenCalledWith(
        parkCollection,
        ...additionalParks.map(expect.objectContaining)
      );
      expect(comp.parksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const report: IReport = { id: 456 };
      const equipement: IEquipement = { id: 39450 };
      report.equipement = equipement;
      const park: IPark = { id: 43697 };
      report.park = park;

      activatedRoute.data = of({ report });
      comp.ngOnInit();

      expect(comp.equipementsSharedCollection).toContain(equipement);
      expect(comp.parksSharedCollection).toContain(park);
      expect(comp.report).toEqual(report);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReport>>();
      const report = { id: 123 };
      jest.spyOn(reportFormService, 'getReport').mockReturnValue(report);
      jest.spyOn(reportService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ report });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: report }));
      saveSubject.complete();

      // THEN
      expect(reportFormService.getReport).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reportService.update).toHaveBeenCalledWith(expect.objectContaining(report));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReport>>();
      const report = { id: 123 };
      jest.spyOn(reportFormService, 'getReport').mockReturnValue({ id: null });
      jest.spyOn(reportService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ report: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: report }));
      saveSubject.complete();

      // THEN
      expect(reportFormService.getReport).toHaveBeenCalled();
      expect(reportService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReport>>();
      const report = { id: 123 };
      jest.spyOn(reportService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ report });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reportService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEquipement', () => {
      it('Should forward to equipementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(equipementService, 'compareEquipement');
        comp.compareEquipement(entity, entity2);
        expect(equipementService.compareEquipement).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
