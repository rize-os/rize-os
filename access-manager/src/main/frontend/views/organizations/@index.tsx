import { useEffect, useState } from 'react';
import { OrganizationEndpoint } from "Frontend/generated/endpoints";
import { Icon, TextField, Button, Notification, HorizontalLayout } from "@vaadin/react-components";
import OrganizationItem from "Frontend/components/organization-item";
import OrganizationDialog from "Frontend/dialogs/organization-dialog";
import Skeleton from "Frontend/components/skeleton";
import Organization from "Frontend/generated/rize/os/access/manager/organization/Organization";

export default function OrganizationView() {


    const [dialogOpened, setDialogOpened] = useState<boolean>(false);
    const [dialogMode, setDialogMode] = useState<"edit" | "create" | undefined>("create");
    const [selectedOrganization, setSelectedOrganization] = useState<Organization | undefined>(undefined);

    const [fetchError, setFetchError] = useState<string>("");
    const [organizations, setOrganizations] = useState<Organization[]>([]);
    const [searchTerm, setSearchTerm] = useState<string>("");
    const [searching, setSearching] = useState<boolean>(false);
    const [initialLoading, setInitialLoading] = useState<boolean>(true);

    const handleOrganizationEdit = (organization: Organization) => {
        setDialogMode("edit");
        setDialogOpened(true);
        setSelectedOrganization(organization);
    }

    const handleOrganizationCreate = () => {
        setDialogMode("create");
        setDialogOpened(true);
        setSelectedOrganization(undefined);
    }

    const handleOrganizationCreated = (organization: Organization) => {
        setOrganizations([...organizations, organization]);
    }

    const handleOrganizationUpdated = (organization: Organization) => {
        setOrganizations(organizations.map(o => o.id === organization.id ? organization : o));
    }

    const handleRetry = async () => {
        await fetchOrganizations();
    }

    const fetchOrganizations = async () => {
        setSearching(searchTerm.length > 0);

        try {
            if (searchTerm)
                setOrganizations(await OrganizationEndpoint.find(searchTerm));
            else
                setOrganizations(await OrganizationEndpoint.findAll());

            setFetchError("");
        } catch (e) {
            setFetchError("An error occurred while fetching organizations");
        }

        setInitialLoading(false);
        setSearching(false);
    };

    useEffect(() => {
        const delayDebounceFn = setTimeout(fetchOrganizations, 500);

        return () => {
            clearTimeout(delayDebounceFn);
        }
    }, [searchTerm]);

    // @ts-ignore
    return (
        <div className={"flex flex-col h-full"} key={"organizationView"}>
            <header key={"header"} className={"flex gap-4 p-6 pb-4 box-border backdrop-blur bg-white/80 sticky top-0 z-50"}>
                <h2 className={"flex-grow text-2xl leading-10"}>Your Organizations</h2>
                <TextField key={"searchField"}
                           placeholder={"Search..."}
                           clearButtonVisible
                           value={searchTerm}
                           onValueChanged={e => setSearchTerm(e.detail.value)}>
                    { searching ? <Icon icon={"vaadin:spinner"} className={"animate-spin"} slot={"prefix"}/> : <Icon icon={"vaadin:search"} slot={"prefix"}/> }
                </TextField>
                <Button theme={"primary"} onClick={handleOrganizationCreate}>
                    <Icon icon={"vaadin:plus"} slot={"prefix"}/>
                    <span>Create</span>
                </Button>
            </header>

            <main>
                <div className={"grid gap-6 p-6 pt-2 box-border"}
                     style={{gridTemplateColumns: "repeat(auto-fill, minmax(22rem, 1fr))"}}>

                    {/* Skeletons */}
                    {initialLoading && Array.from({length: 5}).map((_, i) => (
                        <Skeleton key={i} className={"h-36 w-full"}/>
                    ))}

                    {/* No Organization Placeholder */}
                    {!initialLoading && organizations.length === 0 && (
                        <p className={"text-gray-500"}>No organizations found</p>
                    )}

                    {/* Organization-Items */}
                    {organizations.map((organization) => (
                        <OrganizationItem key={organization.id} organization={organization} onEdit={handleOrganizationEdit}/>
                    ))}
                </div>
            </main>

            <OrganizationDialog mode={dialogMode}
                                opened={dialogOpened}
                                organization={selectedOrganization}
                                onCreate={handleOrganizationCreated}
                                onUpdate={handleOrganizationUpdated}
                                onClose={() => setDialogOpened(false)}/>

            <Notification
                theme="error"
                position="bottom-end"
                opened={fetchError !== ""}
                duration={0}
            >
                <HorizontalLayout theme="spacing" style={{ alignItems: 'center' }}>
                    <Icon icon="vaadin:warning" />
                    <div>{fetchError}</div>
                    <Button style={{ margin: '0 0 0 var(--lumo-space-l)' }} onClick={handleRetry}>
                        Retry
                    </Button>
                </HorizontalLayout>
            </Notification>
        </div>
    );
}
