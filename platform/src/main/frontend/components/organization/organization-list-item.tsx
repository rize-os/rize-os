import React, { useEffect } from "react";
import { Tooltip } from '@vaadin/react-components';
import { ListItem, MenuBarItem, createMenuBarItem } from "Frontend/components/core/list-item";
import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";

interface OrganizationListItemProps {
    organization: OrganizationDto,
    regionsEnabled: boolean,
    onClick?: () => void,
    onEdit?: () => void,
    onDelete?: () => void
}

const OrganizationListItem: React.FC<OrganizationListItemProps> = ({ organization, onClick, onEdit, onDelete, regionsEnabled }) => {

    const [menuBarItems, setMenuBarItems] = React.useState<MenuBarItem[]>([]);

    useEffect(() => {
        initMenuBarItems();
    }, []);

    const initMenuBarItems = () => {
        const menuBarItems = [] as MenuBarItem[];
        if (onClick !== undefined)
            menuBarItems.push(createMenuBarItem("info", "Show", "show", onClick));

        if (onEdit !== undefined)
            menuBarItems.push(createMenuBarItem("edit", "Edit", "edit", onEdit));

        if (onDelete !== undefined) {
            menuBarItems.push({ component: 'hr', className: "m-0.5 dark:border-slate-600" });
            menuBarItems.push(createMenuBarItem("delete", "Delete", "delete", onDelete, "text-red-500 dark:text-red-400", ));
        }

        setMenuBarItems(menuBarItems);
    }

    return (
        <ListItem menuBarItems={menuBarItems}>
            <section className={"flex flex-row gap-3 grow select-none " + (onClick && "cursor-pointer")} onClick={onClick}>
                <div className={"grid items-center justify-items-center size-12 min-w-12 bg-slate-200 dark:bg-slate-600 rounded-lg"}>
                    <span className="material-icons text-slate-500 dark:text-slate-300 text-4xl">apartment</span>
                </div>

                <div className={"flex flex-col"}>
                    <span className={"text-xl leading-7 line-clamp-1"}>{organization.displayName}</span>
                    <span className={"text-xs leading-4 line-clamp-1 text-slate-400"}>{organization.name}</span>
                </div>
            </section>

            <section className={"flex flex-row-reverse gap-1"}>
                { regionsEnabled &&
                    <span {...{theme: 'badge'}} className={"badge hidden sm:block"} id={"organization-region-" + organization.id}>
                        {organization.region}
                    </span>
                }

                { !organization.enabled &&
                    <span {...{theme: 'badge error'}} className={"badge"}>
                        Disabled
                    </span>
                }

                <Tooltip for={"organization-region-" + organization.id} position={"bottom"} text={"Region"}/>
            </section>
        </ListItem>
    );
}

export default OrganizationListItem;