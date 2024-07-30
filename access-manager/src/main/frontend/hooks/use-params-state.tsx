import { useSearchParams } from "react-router-dom";

export function useParamsState(
    paramName: string,
    defaultValue: string
): readonly [
    paramsState: string,
    setParamsState: (newState: string) => void
] {
    const [params, setParams] = useSearchParams();

    const acquiredParam = params.get(paramName);
    const paramsState = acquiredParam ?? defaultValue;

    const setParamsState = (newState: string) => {
        const next = Object.assign(
            {},
            [...params.entries()].reduce(
                (o, [key, value]) => ({ ...o, [key]: value }),
                {}
            ),
            { [paramName]: newState }
        );
        setParams(next);
    };
    return [paramsState, setParamsState];
}