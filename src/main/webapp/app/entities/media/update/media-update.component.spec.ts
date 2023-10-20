import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MediaFormService } from './media-form.service';
import { MediaService } from '../service/media.service';
import { IMedia } from '../media.model';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';
import { IReport } from 'app/entities/report/report.model';
import { ReportService } from 'app/entities/report/service/report.service';

import { MediaUpdateComponent } from './media-update.component';

describe('Media Management Update Component', () => {
  let comp: MediaUpdateComponent;
  let fixture: ComponentFixture<MediaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let mediaFormService: MediaFormService;
  let mediaService: MediaService;
  let parkService: ParkService;
  let equipementService: EquipementService;
  let reportService: ReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MediaUpdateComponent],
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
      .overrideTemplate(MediaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MediaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    mediaFormService = TestBed.inject(MediaFormService);
    mediaService = TestBed.inject(MediaService);
    parkService = TestBed.inject(ParkService);
    equipementService = TestBed.inject(EquipementService);
    reportService = TestBed.inject(ReportService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Park query and add missing value', () => {
      const media: IMedia = { id: 456 };
      const park: IPark = { id: 93193 };
      media.park = park;

      const parkCollection: IPark[] = [{ id: 43495 }];
      jest.spyOn(parkService, 'query').mockReturnValue(of(new HttpResponse({ body: parkCollection })));
      const additionalParks = [park];
      const expectedCollection: IPark[] = [...additionalParks, ...parkCollection];
      jest.spyOn(parkService, 'addParkToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ media });
      comp.ngOnInit();

      expect(parkService.query).toHaveBeenCalled();
      expect(parkService.addParkToCollectionIfMissing).toHaveBeenCalledWith(
        parkCollection,
        ...additionalParks.map(expect.objectContaining)
      );
      expect(comp.parksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Equipement query and add missing value', () => {
      const media: IMedia = { id: 456 };
      const equipement: IEquipement = { id: 97033 };
      media.equipement = equipement;

      const equipementCollection: IEquipement[] = [{ id: 19298 }];
      jest.spyOn(equipementService, 'query').mockReturnValue(of(new HttpResponse({ body: equipementCollection })));
      const additionalEquipements = [equipement];
      const expectedCollection: IEquipement[] = [...additionalEquipements, ...equipementCollection];
      jest.spyOn(equipementService, 'addEquipementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ media });
      comp.ngOnInit();

      expect(equipementService.query).toHaveBeenCalled();
      expect(equipementService.addEquipementToCollectionIfMissing).toHaveBeenCalledWith(
        equipementCollection,
        ...additionalEquipements.map(expect.objectContaining)
      );
      expect(comp.equipementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Report query and add missing value', () => {
      const media: IMedia = { id: 456 };
      const report: IReport = { id: 81843 };
      media.report = report;

      const reportCollection: IReport[] = [{ id: 37382 }];
      jest.spyOn(reportService, 'query').mockReturnValue(of(new HttpResponse({ body: reportCollection })));
      const additionalReports = [report];
      const expectedCollection: IReport[] = [...additionalReports, ...reportCollection];
      jest.spyOn(reportService, 'addReportToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ media });
      comp.ngOnInit();

      expect(reportService.query).toHaveBeenCalled();
      expect(reportService.addReportToCollectionIfMissing).toHaveBeenCalledWith(
        reportCollection,
        ...additionalReports.map(expect.objectContaining)
      );
      expect(comp.reportsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const media: IMedia = { id: 456 };
      const park: IPark = { id: 72907 };
      media.park = park;
      const equipement: IEquipement = { id: 89789 };
      media.equipement = equipement;
      const report: IReport = { id: 39176 };
      media.report = report;

      activatedRoute.data = of({ media });
      comp.ngOnInit();

      expect(comp.parksSharedCollection).toContain(park);
      expect(comp.equipementsSharedCollection).toContain(equipement);
      expect(comp.reportsSharedCollection).toContain(report);
      expect(comp.media).toEqual(media);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedia>>();
      const media = { id: 123 };
      jest.spyOn(mediaFormService, 'getMedia').mockReturnValue(media);
      jest.spyOn(mediaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ media });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: media }));
      saveSubject.complete();

      // THEN
      expect(mediaFormService.getMedia).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(mediaService.update).toHaveBeenCalledWith(expect.objectContaining(media));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedia>>();
      const media = { id: 123 };
      jest.spyOn(mediaFormService, 'getMedia').mockReturnValue({ id: null });
      jest.spyOn(mediaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ media: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: media }));
      saveSubject.complete();

      // THEN
      expect(mediaFormService.getMedia).toHaveBeenCalled();
      expect(mediaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedia>>();
      const media = { id: 123 };
      jest.spyOn(mediaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ media });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(mediaService.update).toHaveBeenCalled();
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

    describe('compareReport', () => {
      it('Should forward to reportService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(reportService, 'compareReport');
        comp.compareReport(entity, entity2);
        expect(reportService.compareReport).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
