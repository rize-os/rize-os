import React, {useEffect} from "react";
import { ListItem, MenuBarItem, createMenuBarItem } from "Frontend/components/core/list-item";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";

interface RegionListItemProps {
    region: RegionDto,
    onClick?: () => void,
    onEdit?: () => void,
    onDelete?: () => void
}

const RegionListItem: React.FC<RegionListItemProps> = ({ region, onClick, onEdit, onDelete}) => {

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
                <div className={"flex flex-col"}>
                    <span className={"text-xl leading-7 line-clamp-1"}>{region.displayName}</span>
                    <span className={"text-xs leading-4 line-clamp-1 text-slate-400"}>{region.name}</span>
                </div>
            </section>
        </ListItem>
    );
}

export default RegionListItem;