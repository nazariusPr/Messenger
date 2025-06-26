import Loader from "../components/UI/Loader";
import { useState } from "react";

const withLoading =
  <T extends object>(
    WrappedComponent: React.ComponentType<
      T & { setLoading: (state: boolean) => void }
    >
  ) =>
  (props: T) => {
    const [loading, setLoading] = useState(false);

    return (
      <div style={{ position: "absolute" }}>
        {loading && <Loader />}
        <WrappedComponent {...props} setLoading={setLoading} />
      </div>
    );
  };

export default withLoading;
