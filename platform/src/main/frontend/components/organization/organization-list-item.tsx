import React, { useEffect } from "react";
import { MenuBar, MenuBarItemSelectedEvent, Tooltip } from '@vaadin/react-components';
import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";

interface OrganizationListItemProps {
    organization: OrganizationDto,
    onClick?: () => void,
    onEdit?: () => void,
    onDelete?: () => void
}

const initMenuBarItems = (onEdit: (() => void) | undefined, onDelete: (() => void) | undefined) => {
    const menuBarItems = {
        component: <span className={"material-icons text-2xl cursor-pointer"}>more_vert</span>,
        className: "text-slate-500 rounded-full p-0 m-0",
        theme: "icon tertiary",
        children: [] as any[]
    };

    if (onEdit)
        menuBarItems.children.push(createMenuBarItem("edit", "Edit", "edit"));

    if (onDelete)
        menuBarItems.children.push(createMenuBarItem("delete", "Delete", "delete"));

    return [menuBarItems];
}

const createMenuBarItem = (icon: string, text: string, key: string) => {
    return {
        component:
            <div className={"flex flex-row gap-2"}>
                <span className={"material-icons text-xl"}>{icon}</span>
                <p>{text}</p>
            </div>,
        key: key
    };
}


const OrganizationListItem: React.FC<OrganizationListItemProps> = ({ organization, onClick, onEdit, onDelete }) => {

    const [menuBarItems, setMenuBarItems] = React.useState<any>(null);
    const [menuBarEnabled, setMenuBarEnabled] = React.useState<boolean>(false);

    useEffect(() => {
        setMenuBarItems(initMenuBarItems(onEdit, onDelete));
        setMenuBarEnabled(!!(onEdit || onDelete));
    }, []);

    const handleMenuItemSelected = (e: MenuBarItemSelectedEvent) => {
        // @ts-ignore
        const key = e.detail.value.key;

        if (key === "edit")
            onEdit?.();
        else if (key === "delete")
            onDelete?.();
    }


    return (
        <main className={"flex flex-row p-1.5 gap-2 items-center hover:bg-slate-50 dark:hover:bg-slate-800 cursor-default"}>
            <section className={"flex flex-row gap-2 grow " + (onClick && "cursor-pointer")} onClick={onClick}>
                <div className={"grid items-center justify-items-center size-12 min-w-12 bg-slate-200 dark:bg-slate-600 rounded-lg"}>
                    <span className="material-icons text-slate-500 dark:text-slate-300 text-4xl">apartment</span>
                </div>

                <div className={"flex flex-col"}>
                    <span className={"text-xl leading-7 line-clamp-1"}>{organization.displayName}</span>
                    <span className={"text-sm leading-4 line-clamp-1 text-slate-400 dark:text-slate-300 mb-1"}>{organization.name}</span>
                </div>
            </section>

            <section className={"flex flex-row-reverse gap-1"}>
                <span {...{theme: 'badge'}} className={"badge hidden sm:block"} id={"organization-region-" + organization.id}>
                    {organization.region}
                </span>

                { !organization.enabled &&
                    <span {...{theme: 'badge error'}} className={"badge"}>
                        Disabled
                    </span>
                }

                <Tooltip for={"organization-region-" + organization.id} position={"bottom"} text={"Region"}/>
            </section>

            { menuBarEnabled &&
                <section>
                    <MenuBar theme="icon" items={menuBarItems} onItemSelected={handleMenuItemSelected}/>
                </section>
            }
        </main>
    );
}

export default OrganizationListItem;