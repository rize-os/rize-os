import { useParams } from 'react-router-dom';

export default function OrganizationEditView() {

    const { organizationId } = useParams();

    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-6xl w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow pb-2"}>
                <header
                    className={"flex flex-col sm:flex-row p-4 box-border sm:rounded-lg backdrop-blur bg-white/75 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <h1 className={"flex-grow text-xl lg:text-2xl leading-10 font-medium dark:text-slate-100 content-center"}>{organizationId || "Create new organization"}</h1>
                </header>
            </main>
        </div>
    )
}