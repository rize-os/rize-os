import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import RegionListItem from "Frontend/components/region/region-list-item";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";
import {RegionConfigurationEndpoint, RegionEndpoint} from "Frontend/generated/endpoints";
import {Button, Checkbox, TextField} from "@vaadin/react-components";
import {EndpointError} from "@vaadin/hilla-frontend";
import LoadingRing from "Frontend/components/core/loading-ring";


export default function RegionsView() {

    const navigate = useNavigate();

    const [regionsEnabled, setRegionsEnabled] = useState<boolean>(false);
    const [regions, setRegions] = useState<RegionDto[]>([]);

    const [fetchError, setFetchError] = useState<EndpointError | undefined>(undefined);
    const [initialRegionsEnabledLoading, setInitialRegionsEnabledLoading] = useState<boolean>(true);
    const [initialRegionsLoading, setInitialRegionsLoading] = useState<boolean>(false);

    const fetchRegionsEnabled = async () => {
        setInitialRegionsEnabledLoading(true);
        setFetchError(undefined);

        RegionConfigurationEndpoint.isRegionFeatureEnabled()
            .then(setRegionsEnabled)
            .catch((error: EndpointError) => setFetchError(error))
            .finally(() => setInitialRegionsEnabledLoading(false));
    }

    const fetchRegions = async () => {
        setRegions([]);
        setFetchError(undefined);

        if (!regionsEnabled)
            return;

        setInitialRegionsLoading(true);

        RegionEndpoint.findAll()
            .then(setRegions)
            .catch((error: EndpointError) => setFetchError(error))
            .finally(() => setInitialRegionsLoading(false));
    }

    const handleRegionEnabledChange = (event: Event) => {
        const enabled = (event.target as HTMLInputElement).checked;
        RegionConfigurationEndpoint.setRegionFeatureEnabled(enabled)
            .then(() => setRegionsEnabled(enabled))
            .catch((error: EndpointError) => console.log(error));
    }

    useEffect(() => {
        fetchRegionsEnabled().then();
    }, []);

    useEffect(() => {
        fetchRegions().then();
    }, [regionsEnabled]);


    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-6xl w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow pb-2"}>
                <header className={"flex flex-col sm:flex-row p-4 pb-2 sm:rounded-lg backdrop-blur bg-white/70 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <h1 className={"flex-grow text-xl lg:text-2xl h-12 leading-10 font-medium dark:text-slate-100 content-center"}>Regions</h1>
                    <div className={"flex flex-row gap-2"}>
                        { regionsEnabled &&
                            <>
                                <TextField className={"grow"} placeholder={"Search..."} clearButtonVisible>
                                <span
                                    className={"material-icons text-xl max-w-6 mr-1 mt-0.5 text-slate-700 dark:text-slate-50"}
                                    slot={"prefix"}>search</span>
                                </TextField>

                                <Button theme={"primary"} onClick={() => navigate('/regions/new')}>
                                    <span className={"material-icons text-xl max-w-6 dark:text-slate-50"}
                                      slot={"prefix"}>add</span>
                                    <span className={"leading-1"}>Create</span>
                                </Button>
                            </>
                    }
                    </div>
                </header>

                { fetchError &&
                    <div className={"flex flex-col items-center justify-center mt-4 mb-8"}>
                        <span className={"material-icons text-6xl text-red-500"}>report_problem</span>
                        <span className={"text-lg text-red-500 text-center"}>Failed to load regions...<br></br>
                            <a className={"underline cursor-pointer"} onClick={fetchRegionsEnabled}>Try again!</a>
                        </span>
                    </div>
                }

                <section className={"flex flex-col px-3 mb-4"}>
                    { initialRegionsEnabledLoading && <LoadingRing className={"text-5xl border-slate-300 place-self-center my-3"}/> }

                    { !initialRegionsEnabledLoading && !fetchError &&
                        <Checkbox className={"w-min"}
                                  label="Enabled"
                                  checked={regionsEnabled}
                                  onChange={handleRegionEnabledChange}/>
                    }
                </section>

                { regionsEnabled &&
                    <section className={"flex flex-col"}>
                        { initialRegionsLoading && <LoadingRing className={"text-5xl border-slate-300 place-self-center my-3"}/> }

                        { !initialRegionsLoading && !fetchError && regions.length == 0 &&
                            <div className={"flex flex-col items-center justify-center mt-4 mb-8"}>
                                <span className={"material-icons text-8xl text-slate-300 dark:text-slate-500"}>clear_all</span>
                                <span className={"text-xl leading-3 text-slate-300 dark:text-slate-500"}>No regions found</span>
                            </div>
                        }

                        {regions.map((region) => (
                            <RegionListItem region={region}
                                            key={region.id}
                                            onClick={() => navigate(`/regions/${region.id}`)}
                                            onEdit={region.name=="default" ? undefined : () => navigate(`/regions/edit/${region.id}`)}/>
                        ))}
                    </section>
                }
            </main>
        </div>
    );
}