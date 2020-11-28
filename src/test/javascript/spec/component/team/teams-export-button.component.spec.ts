import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslatePipe } from '@ngx-translate/core';
import { TeamService } from 'app/exercises/shared/team/team.service';
import { TeamsExportButtonComponent } from 'app/exercises/shared/team/teams-import-dialog/teams-export-button.component';
import { ButtonComponent } from 'app/shared/components/button.component';
import { FeatureToggleModule } from 'app/shared/feature-toggle/feature-toggle.module';
import * as chai from 'chai';
import { JhiAlertService, NgJhipsterModule } from 'ng-jhipster';
import { MockModule, MockPipe, MockProvider } from 'ng-mocks';
import { restore, SinonStub, stub } from 'sinon';
import * as sinonChai from 'sinon-chai';
import { mockTeams } from '../../helpers/mocks/service/mock-team.service';
import { ArtemisTestModule } from '../../test.module';
chai.use(sinonChai);
const expect = chai.expect;

describe('TeamsExportButtonComponent', () => {
    let comp: TeamsExportButtonComponent;
    let fixture: ComponentFixture<TeamsExportButtonComponent>;
    let debugElement: DebugElement;
    let teamService: TeamService;
    let alertService: JhiAlertService;

    function resetComponent() {
        comp.teams = mockTeams;
    }

    beforeEach(
        waitForAsync(() => {
            TestBed.configureTestingModule({
                imports: [ArtemisTestModule, MockModule(NgbModule), MockModule(NgJhipsterModule), MockModule(FeatureToggleModule)],
                declarations: [TeamsExportButtonComponent, ButtonComponent, MockPipe(TranslatePipe)],
                providers: [MockProvider(TeamService)],
            }).compileComponents();
        }),
    );
    beforeEach(() => {
        fixture = TestBed.createComponent(TeamsExportButtonComponent);
        comp = fixture.componentInstance;
        debugElement = fixture.debugElement;
        teamService = TestBed.inject(TeamService);
        alertService = TestBed.inject(JhiAlertService);
    });

    describe('exportTeams', () => {
        let exportTeamsStub: SinonStub;
        let alertServiceStub: SinonStub;
        beforeEach(() => {
            resetComponent();
            exportTeamsStub = stub(teamService, 'exportTeams');
            alertServiceStub = stub(alertService, 'error');
        });
        afterEach(() => {
            restore();
        });
        it('should call export teams from team service when called', () => {
            const button = debugElement.nativeElement.querySelector('button');
            button.click();
            expect(exportTeamsStub).to.have.been.called;
        });
        it('should call alert service if team service fails', () => {
            exportTeamsStub.throws({ message: 'test message' });
            const button = debugElement.nativeElement.querySelector('button');
            button.click();
            expect(exportTeamsStub).to.have.been.called;
            expect(alertServiceStub).to.have.been.calledWith('artemisApp.team.errors.studentsWithoutRegistrationNumbers', { students: 'test message' });
        });
    });
});