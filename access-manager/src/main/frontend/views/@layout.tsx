import { AppLayout } from '@vaadin/react-components/AppLayout.js';
import { DrawerToggle } from '@vaadin/react-components/DrawerToggle.js';
import { ProgressBar } from '@vaadin/react-components/ProgressBar.js';
import { SideNav } from '@vaadin/react-components/SideNav.js';
import { SideNavItem } from '@vaadin/react-components/SideNavItem.js';
import { Suspense } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';

export default function MainLayout() {
    const navigate = useNavigate();

    return (
        <AppLayout primarySection="drawer">
            <div slot="drawer" className="flex flex-col justify-between h-full p-m">
                <header className="flex flex-col gap-m">
                    <h1 className="text-l m-0">My application</h1>
                    <SideNav onNavigate={({path}) => path && navigate(path)} location={location}>
                        <SideNavItem path="/example"></SideNavItem>
                    </SideNav>
                </header>
            </div>

            <DrawerToggle slot="navbar" aria-label="Menu toggle"></DrawerToggle>

            <Suspense fallback={<ProgressBar indeterminate={true} className="m-0" />}>
                <Outlet />
            </Suspense>
        </AppLayout>
    );
}