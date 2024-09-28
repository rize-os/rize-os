import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import OrganizationForm from "Frontend/components/organization/organization-form";
import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";
import {OrganizationEndpoint, RegionConfigurationEndpoint, RegionEndpoint} from "Frontend/generated/endpoints";
import {Notification} from "@vaadin/react-components";

export default function OrganizationCreateView() {

    const navigate = useNavigate();

    const [regionsEnabled, setRegionsEnabled] = useState<boolean>(false);
    const [regions, setRegions] = useState<RegionDto[]>([]);

    const fetchRegionsEnabled = async () => {
        RegionConfigurationEndpoint.isRegionFeatureEnabled()
            .then(setRegionsEnabled);
    }

    const fetchRegions = async () => {
        setRegions([]);

        if (!regionsEnabled)
            return;

        RegionEndpoint.findAll()
            .then(setRegions);
    }

    const handleConfirm = async (organization: OrganizationDto) => {
        await OrganizationEndpoint.create(organization);
        Notification.show(`Organization '${organization.displayName}' created successfully`, {theme: "success", position: "bottom-end", duration: 5000});
        navigateToOrganizations();
    }

    const navigateToOrganizations = () => {
        navigate("/organizations");
    }

    useEffect(() => {
        fetchRegionsEnabled().then();
    }, []);

    useEffect(() => {
        fetchRegions().then();
    }, [regionsEnabled]);

    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-2xl w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow p-4 sm:p-8"}>
                <header className={"mb-4 sm:mb-6 backdrop-blur bg-white/75 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <h1 className={"flex-grow text-2xl lg:text-3xl leading-10 font-medium dark:text-slate-100 content-center"}>Create new organization</h1>
                </header>
                <OrganizationForm regions={regions} onConfirm={handleConfirm} onCancel={navigateToOrganizations}/>
            </main>
        </div>
    )
}