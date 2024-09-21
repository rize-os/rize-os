import {useNavigate, useParams} from 'react-router-dom';
import React, {useEffect, useState} from "react";
import {EndpointError} from "@vaadin/hilla-frontend";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";
import {OrganizationEndpoint, RegionConfigurationEndpoint, RegionEndpoint} from "Frontend/generated/endpoints";
import {Button} from "@vaadin/react-components";
import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";
import OrganizationListItem from "Frontend/components/organization/organization-list-item";


export default function RegionView() {

    const navigate = useNavigate();

    const { regionId } = useParams();

    const [regionsEnabled, setRegionsEnabled] = useState<boolean | undefined>(undefined);
    const [region, setRegion] = useState<RegionDto | undefined>(undefined);
    const [organizations, setOrganizations] = useState<OrganizationDto[]>([]);

    const [organizationsLoading, setOrganizationsLoading] = useState<boolean>(true);

    const fetchRegionsEnabled = async () => {
        RegionConfigurationEndpoint.isRegionFeatureEnabled()
            .then(setRegionsEnabled);
    }

    const fetchRegion = async () => {
        setRegion(undefined);

        RegionEndpoint.findById(regionId?.toString() ?? "")
            .then(setRegion);
    }

    const fetchOrganizations = async () => {
        setOrganizations([]);

        OrganizationEndpoint.findByRegion(region?.name ?? "")
            .then(setOrganizations)
            .catch((error: EndpointError) => console.log(error))
            .finally(() => setOrganizationsLoading(false));
    }

    useEffect(() => {
        fetchRegionsEnabled().then();
    }, []);

    useEffect(() => {
        if (regionsEnabled === false)
        {
            navigate("/regions");
            return;
        }

        fetchRegion().then();
    }, [regionsEnabled]);

    useEffect(() => {
        if (region)
            fetchOrganizations().then();
    }, [region]);

    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-6xl w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow pb-2"}>
                <header className={"flex flex-row p-6 box-border sm:rounded-lg backdrop-blur bg-white/75 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <div className={"flex-grow"}>
                        <h1 className={"text-2xl lg:text-3xl lg:leading-10 line-clamp-1 font-medium dark:text-slate-100 content-center"}>{region?.displayName}</h1>
                        <h3 className={"text-lg leading-5 lg:leading-7 line-clamp-1"}>{region?.name}</h3>
                    </div>

                    <Button theme={"primary"} onClick={() => navigate(`/regions/edit/${region?.id}`)}>
                        <span className={"material-icons text-xl max-w-6 dark:text-slate-50"} slot={"prefix"}>
                            edit
                        </span>
                        <span className={"leading-1 hidden md:block"}>Edit Region</span>
                        <span className={"leading-1 block md:hidden"}>Edit</span>
                    </Button>
                </header>

                <section className={"flex flex-col mt-4"}>
                    <div className={"flex flex-row border-b gap-1 mx-4 mb-1 pb-2"}>
                        <span className="material-icons text-slate-500 dark:text-slate-300 text-2xl">apartment</span>
                        <span className={"text-xl leading-6"}>Organizations in Region</span>
                    </div>


                    {!organizationsLoading && organizations.length == 0 &&
                        <div className={"flex flex-col items-center justify-center mt-4 mb-8"}>
                            <span className={"material-icons text-6xl text-slate-300 dark:text-slate-500"}>clear_all</span>
                            <span className={"text-lg leading-3 text-slate-300 dark:text-slate-500"}>No organizations found</span>
                        </div>
                    }

                    { organizations.map((organization) => (
                        <OrganizationListItem organization={organization}
                                              regionsEnabled={false}
                                              key={organization.id}
                                              onClick={() => navigate('/organizations/' + organization.id)}/>
                    ))}
                </section>
            </main>
        </div>
    )
}