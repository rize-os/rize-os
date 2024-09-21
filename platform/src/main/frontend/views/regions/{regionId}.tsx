import {useNavigate, useParams} from 'react-router-dom';
import {useEffect, useState} from "react";
import {EndpointError} from "@vaadin/hilla-frontend";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";
import {RegionConfigurationEndpoint, RegionEndpoint} from "Frontend/generated/endpoints";
import {sortUserPlugins} from "vite";
import {Button} from "@vaadin/react-components";


export default function RegionView() {

    const navigate = useNavigate();

    const { regionId } = useParams();

    const [regionsEnabled, setRegionsEnabled] = useState<boolean | undefined>(undefined);
    const [region, setRegion] = useState<RegionDto | undefined>(undefined);

    const [fetchError, setFetchError] = useState<EndpointError | undefined>(undefined);

    const fetchRegionsEnabled = async () => {
        setFetchError(undefined);

        RegionConfigurationEndpoint.isRegionFeatureEnabled()
            .then(setRegionsEnabled)
            .catch((error: EndpointError) => setFetchError(error));
    }

    const fetchRegion = async () => {
        setRegion(undefined);
        setFetchError(undefined);

        RegionEndpoint.findById(regionId?.toString() ?? "")
            .then(setRegion)
            .catch((error: EndpointError) => setFetchError(error));
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

    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-6xl w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow pb-2"}>
                <header className={"flex flex-col sm:flex-row p-4 box-border sm:rounded-lg backdrop-blur bg-white/75 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <div className={"flex-grow"}>
                        <h1 className={"text-xl lg:text-2xl leading-10 font-medium dark:text-slate-100 content-center"}>{region?.displayName}</h1>
                        <h3 className={"text-sm"}>{region?.name}</h3>
                    </div>

                    <Button theme={"primary"} onClick={() => navigate(`/regions/edit/${region?.id}`)}>
                        <span className={"material-icons text-xl max-w-6 dark:text-slate-50"}
                              slot={"prefix"}>edit
                        </span>
                        <span className={"leading-1"}>Edit Region</span>
                    </Button>
                </header>
            </main>
        </div>
    )
}