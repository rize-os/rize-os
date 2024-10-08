import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import OrganizationListItem from "Frontend/components/organization/organization-list-item";
import LoadingRing from "Frontend/components/core/loading-ring";
import {Button, ConfirmDialog, Notification, TextField, TextFieldValueChangedEvent} from "@vaadin/react-components";
import { EndpointError } from "@vaadin/hilla-frontend";
import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";
import { OrganizationEndpoint } from "Frontend/generated/endpoints";
import { RegionConfigurationEndpoint }  from "Frontend/generated/endpoints";
import {useDebouncedCallback} from "use-debounce";

export default function OrganizationsView() {

    const navigate = useNavigate();

    const [regionsEnabled, setRegionsEnabled] = useState<boolean>(false);
    const [organizations, setOrganizations] = useState<OrganizationDto[]>([]);
    const [organizationToDelete, setOrganizationToDelete] = useState<OrganizationDto | undefined>(undefined);

    const [searchTerm, setSearchTerm] = useState<string>("");
    const [fetchError, setFetchError] = useState<EndpointError | undefined>(undefined);
    const [deleteError, setDeleteError] = useState<EndpointError | undefined>(undefined);
    const [initialLoading, setInitialLoading] = useState<boolean>(true);

    const fetchRegionsEnabled = async () => {
        RegionConfigurationEndpoint.isRegionFeatureEnabled()
            .then(setRegionsEnabled)
            .catch((error: EndpointError) => console.log(error));
    }

    const fetchOrganizations = async () => {
        setOrganizations([]);
        setInitialLoading(true);
        setFetchError(undefined);

        if (searchTerm.length === 0) {
            OrganizationEndpoint.findAll()
                .then(setOrganizations)
                .catch((error: EndpointError) => setFetchError(error))
                .finally(() => setInitialLoading(false));
            return;
        }

        OrganizationEndpoint.findBySearchTerm(searchTerm)
            .then(setOrganizations)
            .catch((error: EndpointError) => setFetchError(error))
            .finally(() => setInitialLoading(false));
    }

    const handleSearchTermChange = (e: TextFieldValueChangedEvent) => {
        setSearchTerm(e.detail.value);
    }

    const confirmDeleteOrganization = async () => {
        if (!organizationToDelete)
            return;

        OrganizationEndpoint.delete(organizationToDelete.id as string)
            .then(() => {
                setOrganizations(organizations.filter(o => o.id !== organizationToDelete.id));
                resetOrganizationToDelete();
            })
            .catch((error: EndpointError) => { setDeleteError(error) });
    }

    const resetOrganizationToDelete = () => {
        setOrganizationToDelete(undefined);
        setDeleteError(undefined);
    }

    useEffect(() => {
        fetchRegionsEnabled().then();
        fetchOrganizations().then();
    }, []);

    useEffect(() => {
        fetchOrganizations().then();
    }, [searchTerm]);

    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-6xl w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow pb-2"}>
                <header className={"flex flex-col sm:flex-row p-6 sm:rounded-lg backdrop-blur bg-white/70 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <div className={"flex-grow flex flex-row gap-1 sm:gap-2 pb-1 sm:pb-0"}>
                        <span className={"material-icons text-4xl sm:text-5xl"}>apartment</span>
                        <h1 className={"text-2xl lg:text-3xl font-medium content-center"}>Organizations</h1>
                    </div>

                    <div className={"flex flex-row gap-2"}>
                        <TextField className={"grow"}
                                   placeholder={"Search..."}
                                   onValueChanged={useDebouncedCallback(handleSearchTermChange, 500)}
                                   clearButtonVisible
                        >
                            <span className={"material-icons text-xl max-w-6 mr-1 mt-0.5 text-slate-700 dark:text-slate-50"} slot={"prefix"}>search</span>
                        </TextField>

                        <Button theme={"primary"} onClick={() => navigate('/organizations/create')}>
                            <span className={"material-icons text-xl max-w-6 dark:text-slate-50"} slot={"prefix"}>add</span>
                            <span className={"leading-1"}>Create</span>
                        </Button>
                    </div>
                </header>


                <section className={"flex flex-col"}>
                    { initialLoading && <LoadingRing className={"text-5xl border-slate-300 place-self-center my-3"}/> }

                    { !initialLoading && !fetchError && organizations.length == 0 &&
                        <div className={"flex flex-col items-center justify-center mt-4 mb-8"}>
                            <span className={"material-icons text-8xl text-slate-300 dark:text-slate-500"}>clear_all</span>
                            <span className={"text-xl leading-3 text-slate-300 dark:text-slate-500"}>No organizations found</span>
                        </div>
                    }

                    { fetchError &&
                        <div className={"flex flex-col items-center justify-center mt-4 mb-8"}>
                            <span className={"material-icons text-6xl text-red-500"}>report_problem</span>
                            <span className={"text-lg text-red-500 text-center"}>Failed to load organizations...<br></br>
                                <a className={"underline cursor-pointer"} onClick={fetchOrganizations}>Try again!</a>
                            </span>
                        </div>
                    }

                    { organizations.map((organization) => (
                        <OrganizationListItem organization={organization}
                                              regionsEnabled={regionsEnabled}
                                              key={organization.id}
                                              onClick={() => navigate('/organizations/' + organization.id)}
                                              onEdit={() => navigate('/organizations/edit/' + organization.id)}
                                              onDelete={() => setOrganizationToDelete(organization)}/>
                    ))}
                </section>
            </main>

            <ConfirmDialog header={`Delete "${organizationToDelete ? organizationToDelete.displayName : ""}"?`}
                           message={"Are you sure you want to permanently delete this organization?"}
                           cancelButtonVisible confirmText="Delete"
                           confirmTheme={"error primary"}
                           opened={ organizationToDelete !== undefined && deleteError === undefined }
                           onCancel={ () => setOrganizationToDelete(undefined) }
                           onConfirm={ confirmDeleteOrganization }/>

            <Notification
                theme="error"
                duration={0}
                position="bottom-center"
                opened={deleteError !== undefined}>
                <div className={"flex flex-row items-center gap-4"}>
                    <span>Failed to delete organization</span>
                    <Button theme="tertiary-inline"
                            className={"h-8 p-0 ml-4"}
                            onClick={ confirmDeleteOrganization }
                    >
                        Retry
                    </Button>

                    <Button theme="tertiary-inline icon"
                            className={"h-8 p-0"}
                            onClick={ resetOrganizationToDelete }>
                        <span className="material-icons text-2xl pt-1">close</span>
                    </Button>
                </div>
            </Notification>
        </div>
    );
}

