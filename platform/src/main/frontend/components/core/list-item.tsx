import React, {useEffect} from "react";
import { MenuBar, MenuBarItemSelectedEvent } from "@vaadin/react-components";

interface ListItemProps {
    children?: React.ReactNode;
    menuBarItems?: MenuBarItem[];
    onMenuItemSelected?: (key: string) => void;
}

interface MenuBarItem {
    component: React.ReactNode;
    className?: string;
    key?: string;
    onItemSelected?: (e: MenuBarItemSelectedEvent) => void;
}

const createMenuBar = (menuBarItems: MenuBarItem[]): any => {
    const menuBar = {
        component: <span className={"material-icons text-2xl cursor-pointer"}>more_vert</span>,
        className: "text-slate-500 rounded-full p-0 m-0",
        theme: "icon tertiary",
        children: [] as MenuBarItem[]
    };

    menuBarItems.forEach(item => {
        menuBar.children.push(item);
    });

    return [menuBar];
}

const createMenuBarItem = (icon: string, text: string, key: string, onItemSelected: (e: MenuBarItemSelectedEvent) => void, className?: string): MenuBarItem => {
    return {
        component:
            <div className={"flex flex-row gap-2 " + (className)}>
                <span className={"material-icons text-xl max-w-8"}>{icon}</span>
                <p>{text}</p>
            </div>,
        key: key,
        onItemSelected: onItemSelected
    };
}

const ListItem: React.FC<ListItemProps> = ({ children, menuBarItems }) => {

    const [menuBar, setMenuBar] = React.useState<any>(undefined);

    const handleMenuItemSelected = (e: MenuBarItemSelectedEvent) => {
        // @ts-ignore
        e.detail.value.onItemSelected(e);
    }

    useEffect(() => {
        if (menuBarItems)
            setMenuBar(createMenuBar(menuBarItems));
    }, [menuBarItems]);

    return (
        <main className={"flex flex-row px-4 py-2 gap-2 items-center hover:bg-slate-50 dark:hover:bg-slate-800 cursor-default"}>
            {children}

            { menuBar &&
                <section>
                    <MenuBar theme="icon" items={menuBar} onItemSelected={handleMenuItemSelected}/>
                </section>
            }
        </main>
    );
}

export { MenuBarItem, createMenuBarItem, ListItem };